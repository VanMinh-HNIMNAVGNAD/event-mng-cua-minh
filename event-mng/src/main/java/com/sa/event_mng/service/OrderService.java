package com.sa.event_mng.service;

import com.sa.event_mng.dto.response.OrderResponse;
import com.sa.event_mng.exception.AppException;
import com.sa.event_mng.exception.ErrorCode;
import com.sa.event_mng.mapper.OrderMapper;
import com.sa.event_mng.model.entity.*;
import com.sa.event_mng.model.enums.OrderStatus;
import com.sa.event_mng.model.enums.PaymentMethod;
import com.sa.event_mng.model.enums.PaymentStatus;
import com.sa.event_mng.model.enums.TicketStatus;
import com.sa.event_mng.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    TicketRepository ticketRepository;
    TicketTypeRepository ticketTypeRepository;
    OrderMapper orderMapper;
    EmailService emailService;
    VoucherService voucherService;
    InvoiceService invoiceService;

    @Transactional
    public OrderResponse checkout(PaymentMethod paymentMethod, String voucherCode) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        return createOrderFromItems(user, cart.getItems(), paymentMethod, cart, voucherCode);
    }

    @Transactional
    public OrderResponse checkoutSelected(List<Long> itemIds, PaymentMethod paymentMethod, String voucherCode) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> itemIds.contains(item.getId()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        return createOrderFromItems(user, selectedItems, paymentMethod, cart, voucherCode);
    }

    private OrderResponse createOrderFromItems(User user, List<CartItem> items, PaymentMethod paymentMethod, Cart cart, String voucherCode) {
        // Tổng tiền gốc
        BigDecimal subTotal = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính toán giảm giá nếu có voucher
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucherCode != null && !voucherCode.isBlank()) {
            // Lấy eventId từ item đầu tiên (Giả định 1 đơn hàng thanh toán cho 1 sự kiện hoặc voucher toàn sàn)
            Long eventId = items.get(0).getTicketType().getEvent().getId();
            Double discount = voucherService.calculateDiscount(voucherCode, subTotal.doubleValue(), eventId);
            discountAmount = BigDecimal.valueOf(discount);
        }

        // Tổng tiền thực tế khách phải trả
        BigDecimal totalAmount = subTotal.subtract(discountAmount);

        // Phần trăm hoa hồng (25%)
        float platformFeeRate = 0.25f;

        // Tiền admin nhận (Dựa trên tổng tiền thực tế)
        BigDecimal serviceFee = totalAmount.multiply(BigDecimal.valueOf(platformFeeRate));

        // Tiền BTC thực nhận
        BigDecimal organizerAmount = totalAmount.subtract(serviceFee);

        // Validate trạng thái sự kiện
        for (CartItem item : items) {
            if (item.getTicketType().getEvent().getStatus() != com.sa.event_mng.model.enums.EventStatus.OPENING) {
                throw new AppException(ErrorCode.EVENT_NOT_OPENING);
            }
        }

        Order order = Order.builder()
                .customer(user)
                .organizerAmount(organizerAmount)
                .serviceFee(serviceFee)
                .platformFeeRate(platformFeeRate)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .voucherCode(voucherCode)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = items.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .ticketType(cartItem.getTicketType())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .subtotal(cartItem.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Giả lập thanh toán
        completePayment(savedOrder.getId());

        // Xóa các item đã thanh toán khỏi giỏ hàng
        cart.getItems().removeAll(items);
        cartRepository.save(cart);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaidAt(LocalDateTime.now());

        // Generate Tickets
        for (OrderItem item : order.getItems()) {
            TicketType tt = item.getTicketType();

            // Check inventory
            if (tt.getRemainingQuantity() < item.getQuantity()) {
                throw new AppException(ErrorCode.TICKET_NOT_ENOUGH);
            }
            tt.setRemainingQuantity(tt.getRemainingQuantity() - item.getQuantity());
            ticketTypeRepository.save(tt);

            for (int i = 0; i < item.getQuantity(); i++) {
                String ticketCode = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Ticket ticket = Ticket.builder()
                        .order(order)
                        .ticketType(tt)
                        .ticketCode(ticketCode)
                        .qrCode("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + ticketCode)
                        .status(TicketStatus.VALID)
                        .build();
                ticketRepository.save(ticket);
            }
        }
        orderRepository.save(order);

        // Đánh dấu voucher đã sử dụng if any
        if (order.getVoucherCode() != null && !order.getVoucherCode().isBlank()) {
            voucherService.applyVoucher(order.getVoucherCode());
        }

        if (order.getCustomer().getEmail() != null) {
            try {
                byte[] invoicePdf = invoiceService.generateInvoicePdf(order);
                emailService.sendOrderConfirmationWithInvoice(
                        order.getCustomer().getEmail(),
                        order.getId().toString(),
                        order.getTotalAmount(),
                        invoicePdf
                );
            } catch (Exception e) {
                // Log but don't failpayment if email fails
                System.err.println("Failed to send invoice email: " + e.getMessage());
            }
        }
    }

    public Page<OrderResponse> getMyOrders(PageRequest pageRequest) {
        User user = getCurrentUser();
        Page<Order> orderPage = orderRepository.findByCustomerId(user.getId(),pageRequest);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}

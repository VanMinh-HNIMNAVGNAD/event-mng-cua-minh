package com.sa.event_mng.modules.ordering.application.service;

import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.ordering.application.dto.response.OrderResponse;
import com.sa.event_mng.shared.exception.AppException;
import com.sa.event_mng.shared.exception.ErrorCode;
import com.sa.event_mng.modules.ordering.application.mapper.OrderMapper;
import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.ordering.domain.model.OrderItem;
import com.sa.event_mng.modules.ordering.domain.model.Cart;
import com.sa.event_mng.modules.ordering.domain.model.CartItem;
import com.sa.event_mng.model.enums.OrderStatus;
import com.sa.event_mng.model.enums.PaymentMethod;
import com.sa.event_mng.model.enums.PaymentStatus;
import com.sa.event_mng.model.enums.TicketStatus;
import com.sa.event_mng.modules.ticketing.domain.model.Ticket;
import com.sa.event_mng.modules.ordering.domain.repository.OrderRepository;
import com.sa.event_mng.modules.ordering.domain.repository.CartRepository;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.event.domain.repository.TicketTypeRepository;
import com.sa.event_mng.modules.event.domain.model.EventStatus;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.event.domain.model.TicketType;
import com.sa.event_mng.modules.ticketing.domain.repository.TicketRepository;
import com.sa.event_mng.shared.infrastructure.email.EmailService;
import com.sa.event_mng.shared.infrastructure.pdf.PdfService;
import com.sa.event_mng.modules.marketing.application.service.VoucherService;
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
    PdfService pdfService;

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
        BigDecimal subTotal = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validate sale period
        Event event = items.get(0).getTicketType().getEvent();
        LocalDateTime now = LocalDateTime.now();
        if (event.getSaleStartDate() != null && now.isBefore(event.getSaleStartDate())) {
            throw new AppException(ErrorCode.EVENT_NOT_OPENING);
        }
        if (event.getSaleEndDate() != null && now.isAfter(event.getSaleEndDate())) {
            throw new AppException(ErrorCode.EVENT_NOT_OPENING);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucherCode != null && !voucherCode.isBlank()) {
            Long eventId = items.get(0).getTicketType().getEvent().getId();
            Double discount = voucherService.calculateDiscount(voucherCode, subTotal.doubleValue(), eventId);
            discountAmount = BigDecimal.valueOf(discount);
        }

        BigDecimal totalAmount = subTotal.subtract(discountAmount);

        BigDecimal platformFeeRate = new BigDecimal("0.25");
        BigDecimal serviceFee = totalAmount.multiply(platformFeeRate);
        BigDecimal organizerAmount = totalAmount.subtract(serviceFee);

        for (CartItem item : items) {
            if (item.getTicketType().getEvent().getStatus() != EventStatus.OPENING) {
                throw new AppException(ErrorCode.EVENT_NOT_OPENING);
            }
        }

        Order order = Order.builder()
                .customer(user)
                .organizerAmount(organizerAmount)
                .serviceFee(serviceFee)
                .platformFeeRate(platformFeeRate.floatValue())
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

        completePayment(savedOrder.getId());

        if (voucherCode != null && !voucherCode.isBlank()) {
            voucherService.applyVoucher(voucherCode);
        }

        cart.getItems().removeAll(items);
        cartRepository.save(cart);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        try {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setPaidAt(LocalDateTime.now());

            if (order.getTickets() == null) order.setTickets(new java.util.ArrayList<>());
            
            for (OrderItem item : order.getItems()) {
                TicketType tt = item.getTicketType();

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
                    order.getTickets().add(ticket);
                }
            }
            orderRepository.save(order);

            // Gửi email sau khi đã lưu DB thành công
            if (order.getCustomer().getEmail() != null) {
                byte[] pdfBytes = pdfService.generateOrderInvoice(order);
                emailService.sendOrderConfirmationWithInvoice(
                        order.getCustomer().getEmail(),
                        order,
                        pdfBytes
                );
            }
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public byte[] getOrderInvoice(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        
        // Kiểm tra xem đơn hàng có thuộc về người dùng hiện tại không
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return pdfService.generateOrderInvoice(order);
    }
    public Page<OrderResponse> getMyOrders(PageRequest pageRequest) {
        User user = getCurrentUser();
        Page<Order> orderPage = orderRepository.findByCustomerId(user.getId(), pageRequest);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}

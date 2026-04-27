package com.sa.event_mng.service;

import com.sa.event_mng.exception.AppException;
import com.sa.event_mng.exception.ErrorCode;
import com.sa.event_mng.model.entity.Voucher;
import com.sa.event_mng.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    com.sa.event_mng.repository.EventRepository eventRepository;
    com.sa.event_mng.repository.UserRepository userRepository;
    com.sa.event_mng.mapper.VoucherMapper voucherMapper;

    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public com.sa.event_mng.dto.response.VoucherResponse createVoucher(com.sa.event_mng.dto.request.VoucherRequest request) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.sa.event_mng.model.entity.User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Voucher code already exists");
        }

        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setCreator(creator);

        if (request.getEventId() != null) {
            com.sa.event_mng.model.entity.Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
            
            // Nếu là Organizer, chỉ được tạo mã cho sự kiện của mình
            if (creator.getRoles().stream().anyMatch(r -> r.getName().equals("ORGANIZER")) &&
                !event.getOrganizer().getId().equals(creator.getId())) {
                throw new RuntimeException("Not allowed to create voucher for this event");
            }
            voucher.setEvent(event);
        }

        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
    }

    public org.springframework.data.domain.Page<com.sa.event_mng.dto.response.VoucherResponse> getAllVouchers(org.springframework.data.domain.Pageable pageable) {
        return voucherRepository.findAll(pageable).map(voucherMapper::toVoucherResponse);
    }

    public Double calculateDiscount(String code, Double orderAmount, Long eventId) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Cần ErrorCode cụ thể cho Voucher

        // 1. Kiểm tra thời hạn
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            throw new RuntimeException("Voucher is expired or not yet active");
        }

        // 2. Kiểm tra số lượng
        if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
            throw new RuntimeException("Voucher is out of stock");
        }

        // 3. Kiểm tra giá trị đơn hàng tối thiểu
        if (voucher.getMinOrderAmount() != null && BigDecimal.valueOf(orderAmount).compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Order amount does not meet the minimum requirement for this voucher");
        }

        // 4. Kiểm tra xem voucher có giới hạn theo sự kiện không
        if (voucher.getEvent() != null && !voucher.getEvent().getId().equals(eventId)) {
            throw new RuntimeException("Voucher is not applicable for this event");
        }

        // 5. Tính toán số tiền giảm
        BigDecimal discount = BigDecimal.ZERO;
        if ("PERCENTAGE".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = BigDecimal.valueOf(orderAmount).multiply(voucher.getAmount()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else if ("AMOUNT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = voucher.getAmount();
        }

        // Không được giảm quá tổng tiền
        if (discount.compareTo(BigDecimal.valueOf(orderAmount)) > 0) {
            discount = BigDecimal.valueOf(orderAmount);
        }

        return discount.doubleValue();
    }

    @Transactional
    public void applyVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        
        if (voucher.getQuantity() != null && voucher.getQuantity() > 0) {
            voucher.setQuantity(voucher.getQuantity() - 1);
            voucherRepository.save(voucher);
        }
    }
}

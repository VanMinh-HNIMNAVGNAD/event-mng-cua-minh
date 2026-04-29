package com.sa.event_mng.modules.marketing.application.service;

import com.sa.event_mng.shared.exception.AppException;
import com.sa.event_mng.shared.exception.ErrorCode;
import com.sa.event_mng.modules.marketing.domain.model.Voucher;
import com.sa.event_mng.modules.marketing.domain.repository.VoucherRepository;
import com.sa.event_mng.modules.marketing.application.dto.request.VoucherRequest;
import com.sa.event_mng.modules.marketing.application.dto.response.VoucherResponse;
import com.sa.event_mng.modules.marketing.application.mapper.VoucherMapper;
import com.sa.event_mng.modules.event.domain.repository.EventRepository;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.event.domain.model.Event;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    EventRepository eventRepository;
    UserRepository userRepository;
    VoucherMapper voucherMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public VoucherResponse createVoucher(VoucherRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Voucher code already exists");
        }

        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setCreator(creator);

        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
            
            if (creator.getRoles().stream().anyMatch(r -> r.getName().equals("ORGANIZER")) &&
                !event.getOrganizer().getId().equals(creator.getId())) {
                throw new RuntimeException("Not allowed to create voucher for this event");
            }
            voucher.setEvent(event);
        }

        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
    }

    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAll(pageable).map(voucherMapper::toVoucherResponse);
    }

    public Double calculateDiscount(String code, Double orderAmount, Long eventId) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ACTIVE);
        }
        if (now.isAfter(voucher.getEndDate())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);
        }

        if (voucher.getMinOrderAmount() != null && BigDecimal.valueOf(orderAmount).compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new AppException(ErrorCode.VOUCHER_MIN_AMOUNT_NOT_MET);
        }

        if (voucher.getEvent() != null && eventId != null && !voucher.getEvent().getId().equals(eventId)) {
            throw new AppException(ErrorCode.VOUCHER_EVENT_MISMATCH);
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("PERCENTAGE".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = BigDecimal.valueOf(orderAmount).multiply(voucher.getAmount()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else if ("AMOUNT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = voucher.getAmount();
        }

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

package com.sa.event_mng.service;

import com.sa.event_mng.dto.request.VoucherRequest;
import com.sa.event_mng.model.entity.Voucher;
import com.sa.event_mng.model.entity.Event;
import com.sa.event_mng.repository.VoucherRepository;
import com.sa.event_mng.repository.EventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {
    VoucherRepository voucherRepository;
    EventRepository eventRepository;

    @Transactional
    public void createVoucher(VoucherRequest request) {
        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new RuntimeException("EVENT_NOT_FOUND"));
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .type(request.getType())
                .value(request.getValue())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .expiryDate(request.getExpiryDate())
                .event(event)
                .active(true)
                .build();

        voucherRepository.save(voucher);
    }

    public Double calculateDiscount(String code, Double originalAmount, Long eventId) {
        Voucher voucher = voucherRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("VOUCHER_INVALID_OR_EXPIRED"));

        // Check expiry
        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("VOUCHER_EXPIRED");
        }

        // Check usage limit
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("VOUCHER_USAGE_LIMIT_REACHED");
        }

        // Check event restriction
        if (voucher.getEvent() != null && !voucher.getEvent().getId().equals(eventId)) {
            throw new RuntimeException("VOUCHER_NOT_APPLICABLE_FOR_THIS_EVENT");
        }

        double discountAmount = 0;
        if ("PERCENTAGE".equals(voucher.getType())) {
            discountAmount = originalAmount * (voucher.getValue() / 100);
            if (voucher.getMaxDiscount() != null && discountAmount > voucher.getMaxDiscount()) {
                discountAmount = voucher.getMaxDiscount();
            }
        } else if ("FIXED_AMOUNT".equals(voucher.getType())) {
            discountAmount = voucher.getValue();
        }

        return Math.min(discountAmount, originalAmount);
    }

    @Transactional
    public void applyVoucher(String code) {
        Voucher voucher = voucherRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("VOUCHER_NOT_FOUND"));
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
    }
}

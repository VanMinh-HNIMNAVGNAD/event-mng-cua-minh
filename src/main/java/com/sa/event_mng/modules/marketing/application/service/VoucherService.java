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
            throw new AppException(ErrorCode.VOUCHER_EXISTED);
        }

        if (request.getEndDate().isBefore(request.getStartDate().plusHours(1))) {
            throw new AppException(ErrorCode.VOUCHER_DATE_INVALID);
        }

        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setCreator(creator);

        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
            
            if (creator.getRoles().stream().anyMatch(r -> r.getName().equals("ORGANIZER")) &&
                !event.getOrganizer().getId().equals(creator.getId())) {
                throw new AppException(ErrorCode.VOUCHER_NOT_ALLOWED);
            }
            voucher.setEvent(event);
        }

        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
    }

    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (isAdmin) {
            return voucherRepository.findAll(pageable).map(voucherMapper::toVoucherResponse);
        } else {
            return voucherRepository.findByCreatorId(currentUser.getId(), pageable)
                    .map(voucherMapper::toVoucherResponse);
        }
    }

    public Double calculateDiscount(String code, java.util.Map<?, Double> eventAmounts) {
        System.out.println("DEBUG: Voucher Code: " + code);
        System.out.println("DEBUG: Event Amounts Map: " + eventAmounts);

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        // Convert keys to Long to handle potential Jackson type mismatch (String vs Long)
        java.util.Map<Long, Double> normalizedAmounts = new java.util.HashMap<>();
        eventAmounts.forEach((k, v) -> {
            try {
                Long id = Long.valueOf(k.toString());
                normalizedAmounts.put(id, v);
            } catch (Exception e) {
                System.err.println("DEBUG: Failed to parse event ID: " + k);
            }
        });

        Double totalOrderAmount = normalizedAmounts.values().stream().reduce(0.0, Double::sum);

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

        if (voucher.getMinOrderAmount() != null && BigDecimal.valueOf(totalOrderAmount).compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new AppException(ErrorCode.VOUCHER_MIN_AMOUNT_NOT_MET);
        }

        Double applicableAmount = 0.0;
        if (voucher.getEvent() != null) {
            Long targetEventId = voucher.getEvent().getId();
            applicableAmount = normalizedAmounts.getOrDefault(targetEventId, 0.0);
            if (applicableAmount <= 0) {
                throw new AppException(ErrorCode.VOUCHER_EVENT_MISMATCH);
            }
        } else {
            // Check if events belong to the voucher creator
            boolean isCreatorAdmin = voucher.getCreator().getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ADMIN"));
            
            for (java.util.Map.Entry<Long, Double> entry : normalizedAmounts.entrySet()) {
                Long eventId = entry.getKey();
                Double amount = entry.getValue();
                
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
                
                if (isCreatorAdmin || event.getOrganizer().getId().equals(voucher.getCreator().getId())) {
                    applicableAmount += amount;
                }
            }
            
            if (applicableAmount <= 0) {
                throw new AppException(ErrorCode.VOUCHER_EVENT_MISMATCH);
            }
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("PERCENTAGE".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = BigDecimal.valueOf(applicableAmount)
                    .multiply(voucher.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else if ("AMOUNT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = voucher.getAmount();
        }

        if (discount.compareTo(BigDecimal.valueOf(applicableAmount)) > 0) {
            discount = BigDecimal.valueOf(applicableAmount);
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

    @Transactional
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        
        if (!isAdmin && !voucher.getCreator().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        voucherRepository.delete(voucher);
    }

    public java.util.List<VoucherResponse> getActiveVouchersForEvent(Long eventId) {
        System.out.println("DEBUG: Fetching vouchers for event ID: " + eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
        
        System.out.println("DEBUG: Event Organizer ID: " + event.getOrganizer().getId());
        
        java.util.List<Voucher> vouchers = voucherRepository.findActiveVouchersForEvent(eventId, event.getOrganizer().getId(), LocalDateTime.now());
        System.out.println("DEBUG: Found " + vouchers.size() + " active vouchers");
        
        return vouchers.stream()
                .map(voucherMapper::toVoucherResponse)
                .toList();
    }
}

package com.sa.event_mng.modules.marketing.application.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    Long id;
    String code;
    String discountType;
    BigDecimal amount;
    BigDecimal maxDiscount;
    BigDecimal minOrderAmount;
    Integer quantity;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long eventId;
    String eventName;
    String creatorName;
}

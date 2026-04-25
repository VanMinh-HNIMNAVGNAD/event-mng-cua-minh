package com.sa.event_mng.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherRequest {
    private String code;
    private String type; // PERCENTAGE, FIXED_AMOUNT
    private Double value;
    private Double maxDiscount;
    private Integer usageLimit;
    private LocalDateTime expiryDate;
    private Long eventId;
}

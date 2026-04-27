package com.sa.event_mng.modules.event.application.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyRevenueResponse {
    private int year;
    private int month;
    private BigDecimal revenue;
}

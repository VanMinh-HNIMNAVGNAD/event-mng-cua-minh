package com.sa.event_mng.modules.ordering.application.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRevenueStatsAdminResponse {
    private BigDecimal totalRevenue;
    private List<MonthlyRevenueResponse> monthlyRevenues;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueResponse {
        private int year;
        private int month;
        private BigDecimal revenue;
    }
}

package com.sa.event_mng.modules.ordering.application.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueOrganizerResponse {
    private int year;
    private List<MonthlyDetail> months;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDetail {
        private int month;
        private BigDecimal revenue;
    }
}

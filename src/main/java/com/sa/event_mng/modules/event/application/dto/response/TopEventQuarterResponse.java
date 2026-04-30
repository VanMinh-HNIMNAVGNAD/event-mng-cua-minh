package com.sa.event_mng.modules.event.application.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopEventQuarterResponse {
    private int quarter;
    private int year;
    private List<TopEventQuarterResponse.EventItem> events;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventItem {
        private String eventName;
        private Integer ticketsSold;
        private Double occupancyRate;
        private BigDecimal totalRevenue;
        private String status;
    }
}

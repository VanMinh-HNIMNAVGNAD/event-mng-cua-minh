package com.sa.event_mng.modules.event.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class EventYearlyOverviewResponse {
    private int year;
    private List<EventSummary> events;

    @Data
    @Builder
    public static class EventSummary {
        private String eventName;
        private String status;
        private Long ticketsSold;
        private Long totalQuantity;
        private Double sellRate;
        private BigDecimal organizerAmount;
    }
}

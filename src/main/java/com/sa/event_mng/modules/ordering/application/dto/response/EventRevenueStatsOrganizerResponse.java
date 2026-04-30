package com.sa.event_mng.modules.ordering.application.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRevenueStatsOrganizerResponse {
    private String eventName;
    private BigDecimal totalRevenue;
    private Integer ticketsSold;
    private Double percentageOfTicketsSold;
}

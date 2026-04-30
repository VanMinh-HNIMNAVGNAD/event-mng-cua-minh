package com.sa.event_mng.modules.ordering.application.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerOverviewResponse {
    private BigDecimal totalOrganizerAmount;
    private Long totalTicketsSold;
    private Long totalEvents;
    private BigDecimal totalServiceFee;
}

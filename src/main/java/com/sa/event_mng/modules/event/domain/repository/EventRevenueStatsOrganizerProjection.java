package com.sa.event_mng.modules.event.domain.repository;

import java.math.BigDecimal;

public interface EventRevenueStatsOrganizerProjection {
    String getEventName();
    BigDecimal getTotalRevenue();
    Integer getTicketsSold();
    Double getPercentageOfTicketsSold();
}

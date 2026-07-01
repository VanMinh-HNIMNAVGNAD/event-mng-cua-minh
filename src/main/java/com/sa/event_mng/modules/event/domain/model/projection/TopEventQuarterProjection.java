package com.sa.event_mng.modules.event.domain.model.projection;

import java.math.BigDecimal;

public interface TopEventQuarterProjection {
    String getEventName();
    Integer getTicketsSold();
    Double getOccupancyRate();
    BigDecimal getTotalRevenue();
    String getStatus();
}

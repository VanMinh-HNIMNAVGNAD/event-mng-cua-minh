package com.sa.event_mng.modules.event.domain.model.projection;

import java.math.BigDecimal;

public interface EventYearlyOverviewProjection {
    String getEventName();
    String getStatus();
    Long getTicketsSold();
    Long getTotalQuantity();
    BigDecimal getOrganizerAmount();
}

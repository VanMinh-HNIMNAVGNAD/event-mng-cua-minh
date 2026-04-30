package com.sa.event_mng.modules.ordering.domain.model.projection;

import java.math.BigDecimal;

public interface OrganizerOverviewProjection {
    BigDecimal getTotalOrganizerAmount();
    Long getTotalTicketsSold();
    Long getTotalEvents();
    BigDecimal getTotalServiceFee();
}

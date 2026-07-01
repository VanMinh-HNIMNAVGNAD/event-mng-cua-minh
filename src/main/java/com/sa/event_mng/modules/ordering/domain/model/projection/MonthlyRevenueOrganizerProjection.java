package com.sa.event_mng.modules.ordering.domain.model.projection;

import java.math.BigDecimal;

public interface MonthlyRevenueOrganizerProjection {
    int getYear();
    int getMonth();
    BigDecimal getRevenue();
}

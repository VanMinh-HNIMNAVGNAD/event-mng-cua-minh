package com.sa.event_mng.modules.event.domain.repository;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    int getYear();
    int getMonth();
    BigDecimal getRevenue();
}

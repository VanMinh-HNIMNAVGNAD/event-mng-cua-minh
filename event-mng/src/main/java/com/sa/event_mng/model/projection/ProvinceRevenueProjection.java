package com.sa.event_mng.model.projection;

import java.math.BigDecimal;

public interface ProvinceRevenueProjection {
    String getProvince();
    BigDecimal getRevenue();
    Long getOrderCount();
}

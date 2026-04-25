package com.sa.event_mng.model.projection;

import java.math.BigDecimal;

public interface VoucherStatsProjection {
    String getCode();
    Long getUsageCount();
    BigDecimal getTotalDiscount();
}

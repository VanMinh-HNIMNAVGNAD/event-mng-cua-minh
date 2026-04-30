package com.sa.event_mng.modules.event.domain.model.projection;

public interface EventTemporalStatsProjection {
    Integer getHourOfDay();
    Double getPercentageOfTicketsSold();
}

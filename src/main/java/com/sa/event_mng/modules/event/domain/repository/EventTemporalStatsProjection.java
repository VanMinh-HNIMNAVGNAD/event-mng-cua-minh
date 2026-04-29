package com.sa.event_mng.modules.event.domain.repository;

public interface EventTemporalStatsProjection {
    Integer getHourOfDay();
    Double getPercentageOfTicketsSold();
}

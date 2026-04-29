package com.sa.event_mng.modules.event.application.mapper;

import com.sa.event_mng.modules.event.application.dto.response.EventRevenueStatsOrganizerResponse;
import com.sa.event_mng.modules.event.application.dto.response.EventStatusStatsResponse;
import com.sa.event_mng.modules.event.domain.repository.EventRevenueStatsOrganizerProjection;
import com.sa.event_mng.modules.event.domain.repository.EventStatusStatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    @Mapping(target = "percentage", ignore = true)
    @Mapping(target = "countEvents", source = "count")
    EventStatusStatsResponse.EventStatusStatsDetail toEventStatusStatsDetail(EventStatusStatsProjection eventStatusStatsProjection);

    EventRevenueStatsOrganizerResponse toEventRevenueStatsResponse(EventRevenueStatsOrganizerProjection eventRevenueStatsOrganizerProjection);
}

package com.sa.event_mng.modules.event.application.mapper;

import com.sa.event_mng.modules.event.application.dto.response.TicketTypeResponse;
import com.sa.event_mng.modules.event.domain.model.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketTypeMapper {
    TicketTypeResponse toTicketTypeResponse(TicketType ticketType);
}

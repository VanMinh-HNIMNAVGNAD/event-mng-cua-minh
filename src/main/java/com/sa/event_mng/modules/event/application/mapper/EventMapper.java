package com.sa.event_mng.modules.event.application.mapper;

import com.sa.event_mng.modules.event.application.dto.response.EventResponse;
import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.event.domain.model.EventImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { TicketTypeMapper.class, CategoryMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", source = "category")
    @Mapping(target = "organizer", source = "organizer")
    @Mapping(target = "imageUrls", expression = "java(mapImages(event.getImages()))")
    @Mapping(target = "provinceName", source = "province")
    @Mapping(target = "address", source = "location")
    EventResponse toEventResponse(Event event);

    default List<String> mapImages(List<EventImage> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream().map(EventImage::getImageUrl).collect(Collectors.toList());
    }
}

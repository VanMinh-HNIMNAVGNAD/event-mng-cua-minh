package com.sa.event_mng.modules.event.application.mapper;

import com.sa.event_mng.modules.event.application.dto.response.CategoryResponse;
import com.sa.event_mng.modules.event.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}

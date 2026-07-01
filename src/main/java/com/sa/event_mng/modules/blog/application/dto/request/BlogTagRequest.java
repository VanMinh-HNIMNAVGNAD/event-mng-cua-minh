package com.sa.event_mng.modules.blog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogTagRequest {

    @NotBlank(message = "Tên tag không được để trống")
    String name;

    @NotBlank(message = "Slug không được để trống")
    String slug;
}

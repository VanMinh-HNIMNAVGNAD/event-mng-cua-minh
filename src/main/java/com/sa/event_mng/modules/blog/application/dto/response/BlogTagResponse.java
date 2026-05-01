package com.sa.event_mng.modules.blog.application.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogTagResponse {
    Long id;
    String name;
    String slug;
}

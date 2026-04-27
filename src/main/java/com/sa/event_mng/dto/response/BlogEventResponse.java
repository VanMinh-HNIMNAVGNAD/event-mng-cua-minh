package com.sa.event_mng.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogEventResponse {
    Long id;
    String name;
    String location;
    String province;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime saleStartDate;
    LocalDateTime saleEndDate;
    String descriptionStatus;
    String imageUrl;
}

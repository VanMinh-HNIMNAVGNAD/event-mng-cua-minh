package com.sa.event_mng.modules.event.application.dto.response;

import com.sa.event_mng.modules.event.domain.model.EventStatus;
import com.sa.event_mng.modules.identity.application.dto.response.OrganizerResponse;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private OrganizerResponse organizer;
    private String location;
    private String province;
    private String provinceName; // Alias for province
    private String address; // Alias for location
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;
    private String description;
    private EventStatus status;
    private List<String> imageUrls;
    private List<TicketTypeResponse> ticketTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

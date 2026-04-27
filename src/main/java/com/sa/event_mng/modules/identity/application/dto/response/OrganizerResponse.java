package com.sa.event_mng.modules.identity.application.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerResponse {
    private Long id;
    private String fullName;
    private String email;
}

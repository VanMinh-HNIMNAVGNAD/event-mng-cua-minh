package com.sa.event_mng.modules.identity.application.dto.response;

import com.sa.event_mng.modules.identity.domain.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;

    String username;

    String email;

    String fullName;

    String phone;

    String address;

    boolean enabled;

    java.util.Set<Role> roles;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    java.time.LocalDateTime createdAt;
}

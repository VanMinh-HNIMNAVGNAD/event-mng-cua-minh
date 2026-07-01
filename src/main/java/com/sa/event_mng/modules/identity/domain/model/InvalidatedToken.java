package com.sa.event_mng.modules.identity.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidatedToken {
    @Id
    private String id;
    private Date expiryTime;
}

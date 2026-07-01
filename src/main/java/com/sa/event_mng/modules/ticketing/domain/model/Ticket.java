package com.sa.event_mng.modules.ticketing.domain.model;

import com.sa.event_mng.shared.domain.model.BaseEntity;
import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.event.domain.model.TicketType;
import com.sa.event_mng.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @Column(name = "ticket_code", unique = true, nullable = false)
    private String ticketCode;

    @Column(name = "qr_code")
    private String qrCode;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}

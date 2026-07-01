package com.sa.event_mng.faker;

import com.sa.event_mng.model.enums.TicketStatus;
import com.sa.event_mng.modules.ordering.domain.model.OrderItem;
import com.sa.event_mng.modules.ordering.domain.repository.OrderItemRepository;
import com.sa.event_mng.modules.ticketing.domain.model.Ticket;
import com.sa.event_mng.modules.ticketing.domain.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketSeeder {

    private final TicketRepository ticketRepository;
    private final OrderItemRepository orderItemRepository;

    public void seed() {
        if (ticketRepository.count() > 0) return;

        List<OrderItem> items = orderItemRepository.findAll();
        if (items.isEmpty()) return;


        for (OrderItem item : items) {
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = Ticket.builder()
                        .order(item.getOrder())
                        .ticketType(item.getTicketType())
                        .ticketCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                        .qrCode("QR-" + UUID.randomUUID())
                        .status(TicketStatus.VALID)
                        .build();
                ticketRepository.save(ticket);
            }
        }
    }
}

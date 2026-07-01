package com.sa.event_mng.faker;

import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.event.domain.model.TicketType;
import com.sa.event_mng.modules.event.domain.repository.EventRepository;
import com.sa.event_mng.modules.event.domain.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TicketTypeSeeder {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;

    private final Random random = new Random();

    private static final String[] NAMES = {"Vé Thường", "Vé VIP", "Vé Premium", "Vé Early Bird", "Vé Student"};
    private static final long[] BASE_PRICES = {99000, 199000, 399000, 149000, 79000};

    public void seed() {
        if (ticketTypeRepository.count() > 0) return;

        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) return;


        for (Event event : events) {
            int numberOfTypes = random.nextInt(3) + 1;
            for (int i = 0; i < numberOfTypes; i++) {
                int total = random.nextInt(451) + 50;
                int remaining = total - random.nextInt(total + 1);

                TicketType tt = new TicketType();
                tt.setEvent(event);
                tt.setName(NAMES[i % NAMES.length]);
                tt.setPrice(BigDecimal.valueOf(BASE_PRICES[i % BASE_PRICES.length] + random.nextInt(200) * 1000L));
                tt.setTotalQuantity(total);
                tt.setRemainingQuantity(remaining);
                tt.setDescription("Loại vé " + NAMES[i % NAMES.length] + " cho sự kiện " + event.getName());
                ticketTypeRepository.save(tt);
            }
        }
    }
}

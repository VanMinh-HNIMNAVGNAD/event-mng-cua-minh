package com.sa.event_mng.task;

import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.event.domain.model.EventStatus;
import com.sa.event_mng.modules.event.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStatusTask {

    private final EventRepository eventRepository;

    /**
     * Tự động cập nhật trạng thái sự kiện mỗi phút.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoUpdateEventStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // Chỉ quét các sự kiện có khả năng thay đổi trạng thái tự động
        List<EventStatus> activeStatuses = Arrays.asList(
                EventStatus.UPCOMING,
                EventStatus.OPENING,
                EventStatus.CLOSED
        );

        List<Event> events = eventRepository.findByStatusIn(activeStatuses);
        
        for (Event event : events) {
            EventStatus oldStatus = event.getStatus();
            EventStatus newStatus = event.calculateStatus(now);
            
            if (oldStatus != newStatus) {
                event.setStatus(newStatus);
                eventRepository.save(event);
                log.info("Event ID {}: Auto-updated status from {} to {}", event.getId(), oldStatus, newStatus);
            }
        }
    }
}

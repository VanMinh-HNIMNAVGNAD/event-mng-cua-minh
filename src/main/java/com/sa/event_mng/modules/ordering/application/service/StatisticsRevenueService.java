package com.sa.event_mng.modules.ordering.application.service;

import com.sa.event_mng.modules.event.application.mapper.StatsMapper;
import com.sa.event_mng.modules.ordering.domain.model.projection.EventRevenueStatsAdminProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.EventRevenueStatsOrganizerProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.MonthlyRevenueProjection;
import com.sa.event_mng.modules.ordering.domain.repository.StatisticsOrderRepository;
import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsAdminResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsOrganizerResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.MonthlyRevenueResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsRevenueService {

    StatisticsOrderRepository statisticsOrderRepository;
    StatsMapper statsMapper;

    @PreAuthorize("hasRole('ORGANIZER') and @securityCustom.isCurrentUser(#idOrganizer, authentication)")
    public List<EventRevenueStatsOrganizerResponse> getEventRevenueStatsOrganizer(Long idOrganizer) {
        List<EventRevenueStatsOrganizerProjection> eventRevenueStats = statisticsOrderRepository.findEventRevenueOrganizerStats(idOrganizer);
        return eventRevenueStats.stream()
                .map(statsMapper::toEventRevenueStatsResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EventRevenueStatsAdminResponse getEventRevenueStatsAdmin() {
        EventRevenueStatsAdminProjection totalStats = statisticsOrderRepository.findEventRevenueAdminStats();
        List<MonthlyRevenueProjection> dbMonthly = statisticsOrderRepository.findMonthlyRevenueAdmin();

        List<MonthlyRevenueResponse> monthlyList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            int y = date.getYear();
            int m = date.getMonthValue();

            BigDecimal rev = dbMonthly.stream()
                    .filter(p -> p.getYear() == y && p.getMonth() == m)
                    .map(MonthlyRevenueProjection::getRevenue)
                    .findFirst().orElse(BigDecimal.ZERO);

            monthlyList.add(MonthlyRevenueResponse.builder()
                    .year(y)
                    .month(m)
                    .revenue(rev)
                    .build());
        }

        return EventRevenueStatsAdminResponse.builder()
                .totalRevenue(totalStats != null ? totalStats.getTotalRevenue() : BigDecimal.ZERO)
                .monthlyRevenues(monthlyList)
                .build();
    }
}

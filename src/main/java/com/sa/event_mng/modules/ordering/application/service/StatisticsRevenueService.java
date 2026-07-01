package com.sa.event_mng.modules.ordering.application.service;

import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsAdminResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.MonthlyRevenueOrganizerResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.OrganizerOverviewResponse;
import com.sa.event_mng.modules.ordering.domain.model.projection.EventRevenueStatsAdminProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.MonthlyRevenueOrganizerProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.MonthlyRevenueProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.OrganizerOverviewProjection;
import com.sa.event_mng.modules.ordering.domain.repository.StatisticsOrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsRevenueService {

    StatisticsOrderRepository statisticsOrderRepository;
    

//    @PreAuthorize("hasRole('ORGANIZER') and @securityCustom.isCurrentUser(#idOrganizer, authentication)")
//    public List<EventRevenueStatsOrganizerResponse> getEventRevenueStatsOrganizer(Long idOrganizer) {
//        List<EventRevenueStatsOrganizerProjection> eventRevenueStats = statisticsOrderRepository.findEventRevenueOrganizerStats(idOrganizer);
//        return eventRevenueStats.stream()
//                .map(statsMapper::toEventRevenueStatsResponse)
//                .toList();
//    }

    @PreAuthorize("hasRole('ORGANIZER') and @securityCustom.isCurrentUser(#idOrganizer, authentication)")
    public OrganizerOverviewResponse getOrganizerOverview(Long idOrganizer) {
        OrganizerOverviewProjection p = statisticsOrderRepository.findOrganizerOverview(idOrganizer);
        return OrganizerOverviewResponse.builder()
                .totalOrganizerAmount(p != null && p.getTotalOrganizerAmount() != null ? p.getTotalOrganizerAmount() : BigDecimal.ZERO)
                .totalTicketsSold(p != null && p.getTotalTicketsSold() != null ? p.getTotalTicketsSold() : 0L)
                .totalEvents(p != null && p.getTotalEvents() != null ? p.getTotalEvents() : 0L)
                .totalServiceFee(p != null && p.getTotalServiceFee() != null ? p.getTotalServiceFee() : BigDecimal.ZERO)
                .build();
    }

    @PreAuthorize("hasRole('ORGANIZER') and @securityCustom.isCurrentUser(#idOrganizer, authentication)")
    public MonthlyRevenueOrganizerResponse getMonthlyRevenueOrganizer(Long idOrganizer, int year) {
        List<MonthlyRevenueOrganizerProjection> dbMonthly = statisticsOrderRepository.findMonthlyRevenueOrganizer(idOrganizer, year);

        List<MonthlyRevenueOrganizerResponse.MonthlyDetail> monthlyList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int month = m;
            BigDecimal rev = dbMonthly.stream()
                    .filter(p -> p.getMonth() == month)
                    .map(MonthlyRevenueOrganizerProjection::getRevenue)
                    .findFirst().orElse(BigDecimal.ZERO);
            monthlyList.add(MonthlyRevenueOrganizerResponse.MonthlyDetail.builder()
                    .month(month).revenue(rev).build());
        }

        return MonthlyRevenueOrganizerResponse.builder().year(year).months(monthlyList).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EventRevenueStatsAdminResponse getEventServiceRevenueStatsAdmin(int year) {
        EventRevenueStatsAdminProjection totalStats = statisticsOrderRepository.findEventRevenueAdminStats(year);
        List<MonthlyRevenueProjection> dbMonthly = statisticsOrderRepository.findMonthlyRevenueAdmin(year);

        List<EventRevenueStatsAdminResponse.MonthlyRevenueResponse> monthlyList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int month = m;
            BigDecimal rev = dbMonthly.stream()
                    .filter(p -> p.getMonth() == month)
                    .map(MonthlyRevenueProjection::getRevenue)
                    .findFirst().orElse(BigDecimal.ZERO);

            monthlyList.add(EventRevenueStatsAdminResponse.MonthlyRevenueResponse.builder()
                    .year(year)
                    .month(month)
                    .revenue(rev)
                    .build());
        }

        return EventRevenueStatsAdminResponse.builder()
                .totalRevenue(totalStats != null ? totalStats.getTotalRevenue() : BigDecimal.ZERO)
                .monthlyRevenues(monthlyList)
                .build();
    }
}

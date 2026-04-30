package com.sa.event_mng.modules.ordering.presentation.controller;

import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsAdminResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.MonthlyRevenueOrganizerResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.OrganizerOverviewResponse;
import com.sa.event_mng.modules.ordering.application.service.StatisticsRevenueService;
import com.sa.event_mng.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Thống kê doanh thu", description = "Thống kê doanh thu theo đơn hàng")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsRevenueController {

    StatisticsRevenueService statisticsRevenueService;

//    @GetMapping("/statistics-revenue/{id_organizer}")
//    @Operation(summary = "Thống kê doanh thu (chủ sự kiện)")
//    public ApiResponse<List<EventRevenueStatsOrganizerResponse>> getStatisticsRevenueOrganizer(@PathVariable("id_organizer") Long idOrganizer) {
//        return ApiResponse.<List<EventRevenueStatsOrganizerResponse>>builder().result(statisticsRevenueService.getEventRevenueStatsOrganizer(idOrganizer)).build();
//    }

    @GetMapping("/statistics-revenue/{id_organizer}/overview")
    @Operation(summary = "Thống kê tổng quan doanh thu organizer")
    public ApiResponse<OrganizerOverviewResponse> getOrganizerOverview(@PathVariable("id_organizer") Long idOrganizer) {
        return ApiResponse.<OrganizerOverviewResponse>builder()
                .result(statisticsRevenueService.getOrganizerOverview(idOrganizer))
                .build();
    }

    @GetMapping("/statistics-revenue/{id_organizer}/{year}")
    @Operation(summary = "Thống kê doanh thu theo tháng trong năm (chủ sự kiện)")
    public ApiResponse<MonthlyRevenueOrganizerResponse> getMonthlyRevenueOrganizer(
            @PathVariable("id_organizer") Long idOrganizer,
            @PathVariable int year) {
        return ApiResponse.<MonthlyRevenueOrganizerResponse>builder()
                .result(statisticsRevenueService.getMonthlyRevenueOrganizer(idOrganizer, year))
                .build();
    }

    @GetMapping("/statistics-service-revenue/admin/{year}")
    @Operation(summary = "Thống kê doanh thu phí dịch vụ (admin)")
    public ApiResponse<EventRevenueStatsAdminResponse> getStatisticsServiceRevenueAdmin(@PathVariable int year) {
        return ApiResponse.<EventRevenueStatsAdminResponse>builder().result(statisticsRevenueService.getEventServiceRevenueStatsAdmin(year)).build();
    }
}

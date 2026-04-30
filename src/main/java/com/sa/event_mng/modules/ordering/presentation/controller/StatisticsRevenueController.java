package com.sa.event_mng.modules.ordering.presentation.controller;

import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsAdminResponse;
import com.sa.event_mng.modules.ordering.application.dto.response.EventRevenueStatsOrganizerResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Thống kê doanh thu", description = "Thống kê doanh thu theo đơn hàng")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsRevenueController {

    StatisticsRevenueService statisticsRevenueService;

    @GetMapping("/statistics-revenue/{id_organizer}")
    @Operation(summary = "Thống kê doanh thu (chủ sự kiện)")
    public ApiResponse<List<EventRevenueStatsOrganizerResponse>> getStatisticsRevenueOrganizer(@PathVariable("id_organizer") Long idOrganizer) {
        return ApiResponse.<List<EventRevenueStatsOrganizerResponse>>builder().result(statisticsRevenueService.getEventRevenueStatsOrganizer(idOrganizer)).build();
    }

    @GetMapping("/statistics-revenue/admin")
    @Operation(summary = "Thống kê doanh thu (admin, tính tổng tiền dịch vụ)")
    public ApiResponse<EventRevenueStatsAdminResponse> getStatisticsRevenueAdmin() {
        return ApiResponse.<EventRevenueStatsAdminResponse>builder().result(statisticsRevenueService.getEventRevenueStatsAdmin()).build();
    }
}

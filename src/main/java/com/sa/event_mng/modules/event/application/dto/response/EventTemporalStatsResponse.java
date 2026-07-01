package com.sa.event_mng.modules.event.application.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTemporalStatsResponse {
    private String day;
    private List<EventTemporalStatsDetail> eventTemporalStatsDetail;

    @Data
    @AllArgsConstructor
    public static class EventTemporalStatsDetail {
        private Integer hourOfDay;
        private Double percentageOfTicketsSold;
    }
}

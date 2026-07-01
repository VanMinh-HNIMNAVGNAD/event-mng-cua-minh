package com.sa.event_mng.modules.event.application.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatusStatsResponse {
    private Long quarter;
    private Long year;
    private Long total;
    private List<EventStatusStatsDetail> eventStatusStatsDetail;

    @Data
    @AllArgsConstructor
    public static class EventStatusStatsDetail {
        private String status;
        private Double percentage;
        private Long countEvents;
    }
}

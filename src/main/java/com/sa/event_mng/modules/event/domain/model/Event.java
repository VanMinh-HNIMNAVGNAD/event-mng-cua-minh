package com.sa.event_mng.modules.event.domain.model;

import com.sa.event_mng.shared.domain.model.BaseEntity;
import com.sa.event_mng.modules.identity.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private String location;
    private String province;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> images;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> ticketTypes;

    public EventStatus calculateStatus(LocalDateTime now) {
        // Nếu bị hủy hoặc đã hoàn thành từ trước (thủ công) thì giữ nguyên nếu cần
        // Nhưng ở đây ta focus vào auto-update cho các case active
        
        // quá end_time COMPLETED
        if (endTime != null && now.isAfter(endTime)) {
            return EventStatus.COMPLETED;
        }

        // quá thời gian bắt đầu sự kiện (mà chưa có endTime) -> COMPLETED
        if (endTime == null && startTime != null && now.isAfter(startTime)) {
            return EventStatus.COMPLETED;
        }

        // Kiểm tra thời gian bán vé
        if (saleEndDate != null && now.isAfter(saleEndDate)) {
            return EventStatus.CLOSED; // Hết hạn bán vé
        }

        if (saleStartDate != null && (now.isAfter(saleStartDate) || now.isEqual(saleStartDate))) {
            return EventStatus.OPENING; // Đang trong thời gian bán vé
        }

        return EventStatus.UPCOMING;
    }
}

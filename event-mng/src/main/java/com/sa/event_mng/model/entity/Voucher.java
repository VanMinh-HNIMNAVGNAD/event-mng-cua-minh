package com.sa.event_mng.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String type; // PERCENTAGE or FIXED_AMOUNT

    @Column(nullable = false)
    private Double value;

    private Double maxDiscount; // Cho loại PERCENTAGE

    private Integer usageLimit;
    private Integer usedCount;

    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // Voucher dành riêng cho sự kiện nào (nullable nếu cho toàn sàn)

    private boolean active;
}

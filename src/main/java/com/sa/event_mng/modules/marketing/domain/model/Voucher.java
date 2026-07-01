package com.sa.event_mng.modules.marketing.domain.model;

import com.sa.event_mng.shared.domain.model.BaseEntity;
import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.identity.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @Column(name = "discount_type", nullable = false)
    private String discountType; // PERCENTAGE or AMOUNT

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "max_discount")
    private BigDecimal maxDiscount;

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    private Integer quantity;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // Null if it's a global voucher

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User creator;
}

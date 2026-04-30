package com.sa.event_mng.modules.ordering.domain.repository;

import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.ordering.domain.model.projection.EventRevenueStatsAdminProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.EventRevenueStatsOrganizerProjection;
import com.sa.event_mng.modules.ordering.domain.model.projection.MonthlyRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticsOrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
    SELECT e.name as eventName,
        SUM(o.total_amount) as totalRevenue,
        SUM(tt.total_quantity - tt.remaining_quantity) as ticketsSold,
        CASE
            WHEN SUM(tt.total_quantity) = 0 THEN 0
            ELSE SUM(tt.total_quantity - tt.remaining_quantity) * 100.0 / SUM(tt.total_quantity)
        END AS percentageOfTicketsSold
    FROM orders o
    JOIN order_items oi ON o.id = oi.order_id
    JOIN ticket_types tt ON oi.ticket_type_id = tt.id
    RIGHT JOIN events e ON e.id = tt.event_id
    WHERE e.organizer_id = :id
    AND o.payment_status = 'PAID'
    GROUP BY e.id
    """, nativeQuery = true)
    List<EventRevenueStatsOrganizerProjection> findEventRevenueOrganizerStats(@Param("id") Long id);

    @Query(value = """
    SELECT SUM(service_fee) as totalRevenue
    FROM orders
    WHERE payment_status = 'PAID'
    """, nativeQuery = true)
    EventRevenueStatsAdminProjection findEventRevenueAdminStats();

    @Query(value = """
    SELECT YEAR(created_at) as year, MONTH(created_at) as month, SUM(service_fee) as revenue
    FROM orders
    WHERE payment_status = 'PAID'
    GROUP BY YEAR(created_at), MONTH(created_at)
    ORDER BY year DESC, month DESC
    """, nativeQuery = true)
    List<MonthlyRevenueProjection> findMonthlyRevenueAdmin();
}

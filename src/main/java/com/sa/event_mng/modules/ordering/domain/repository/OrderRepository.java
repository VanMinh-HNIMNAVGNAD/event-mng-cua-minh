package com.sa.event_mng.modules.ordering.domain.repository;

import com.sa.event_mng.modules.ordering.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.ticketType it " +
           "LEFT JOIN FETCH it.event " +
           "LEFT JOIN FETCH o.tickets t " +
           "LEFT JOIN FETCH t.ticketType tt " +
           "LEFT JOIN FETCH tt.event " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithTickets(@Param("id") String id);

    Optional<Order> findByOrderCode(Long orderCode);
}

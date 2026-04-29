package com.sa.event_mng.faker;

import com.sa.event_mng.model.enums.OrderStatus;
import com.sa.event_mng.model.enums.PaymentMethod;
import com.sa.event_mng.model.enums.PaymentStatus;
import com.sa.event_mng.modules.event.domain.repository.EventRepository;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.ordering.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderSeeder {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final Random random = new Random();

    public void seed() {
        List<User> customers = userRepository.findByRoles_Name("CUSTOMER");
        List<com.sa.event_mng.modules.event.domain.model.Event> events = eventRepository.findAll();
        if (customers.isEmpty() || events.isEmpty()) return;

        for (User customer : customers) {
            int numberOfOrders = random.nextInt(3) + 1;
            for (int i = 0; i < numberOfOrders; i++) {
                Order order = new Order();
                order.setCustomer(customer);
                order.setOrderStatus(OrderStatus.CONFIRMED);
                order.setPaymentMethod(random.nextBoolean() ? PaymentMethod.BANKING : PaymentMethod.MOMO);
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setTotalAmount(BigDecimal.ZERO);
                order.setServiceFee(BigDecimal.ZERO);
                order.setOrganizerAmount(BigDecimal.ZERO);
                order.setPlatformFeeRate(0.05f);
                order.setOrderDate(LocalDateTime.now().minusDays(random.nextInt(30)));
                order.setPaidAt(LocalDateTime.now().minusDays(random.nextInt(29)));
                orderRepository.save(order);
            }
        }
    }
}

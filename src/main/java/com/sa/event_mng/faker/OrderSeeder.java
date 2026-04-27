/* package com.sa.event_mng.faker;

import com.github.javafaker.Faker;
import com.sa.event_mng.model.entity.Event;
import com.sa.event_mng.model.entity.Order;
import com.sa.event_mng.model.entity.OrderItem;
import com.sa.event_mng.model.entity.TicketType;
import com.sa.event_mng.model.entity.User;
import com.sa.event_mng.model.enums.OrderStatus;
import com.sa.event_mng.model.enums.PaymentMethod;
import com.sa.event_mng.repository.EventRepository;
import com.sa.event_mng.repository.OrderRepository;
import com.sa.event_mng.repository.TicketTypeRepository;
import com.sa.event_mng.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderSeeder implements CommandLineRunner {

    OrderRepository orderRepository;
    UserRepository userRepository;
    EventRepository eventRepository;
    TicketTypeRepository ticketTypeRepository;

    Faker faker = new Faker();
    Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (orderRepository.count() == 0) {
            seedOrders();
        }
    }

    private void seedOrders() {
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("CUSTOMER")))
                .toList();
        List<Event> events = eventRepository.findAll();

        for (User customer : customers) {
            if (events.isEmpty()) break;

            int numberOfOrders = random.nextInt(5) + 1;
            for (int i = 0; i < numberOfOrders; i++) {
                Event event = events.get(random.nextInt(events.size()));
                if (!event.getIsOnline()) continue;

                Order order = createRandomOrder(customer, event);
                orderRepository.save(order);
            }
        }

        System.out.println("Seeded Orders.");
    }

    private Order createRandomOrder(User customer, Event event) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PAID);
        order.setPaymentMethod(random.nextBoolean() ? PaymentMethod.BANK_TRANSFER : PaymentMethod.MOMO);
        order.setOrderedAt(LocalDateTime.now().minusDays(random.nextInt(30)));

        List<TicketType> availableTypes = ticketTypeRepository.findAll().stream()
                .filter(tt -> tt.getEvent().getId().equals(event.getId()))
                .toList();

        if (availableTypes.isEmpty()) return null;

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        int itemCount = random.nextInt(3) + 1;
        for (int j = 0; j < itemCount; j++) {
            TicketType type = availableTypes.get(random.nextInt(availableTypes.size()));
            if (type.getRemainingQuantity() <= 0) continue;

            int quantity = random.nextInt(3) + 1;
            if (quantity > type.getRemainingQuantity()) quantity = type.getRemainingQuantity();

            BigDecimal itemTotal = type.getPrice().multiply(BigDecimal.valueOf(quantity));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setTicketType(type);
            item.setQuantity(quantity);
            item.setPrice(type.getPrice());
            item.setSubTotal(itemTotal);

            orderItems.add(item);
            totalAmount = totalAmount.add(itemTotal);
        }

        if (orderItems.isEmpty()) return null;

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Update remaining quantities
        for (OrderItem item : orderItems) {
            TicketType type = item.getTicketType();
            type.setRemainingQuantity(type.getRemainingQuantity() - item.getQuantity());
            ticketTypeRepository.save(type);
        }

        return order;
    }
}
*/

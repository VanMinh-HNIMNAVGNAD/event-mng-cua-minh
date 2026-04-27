/*
package com.sa.event_mng.faker;

import com.github.javafaker.Faker;
import com.sa.event_mng.model.entity.Cart;
import com.sa.event_mng.model.entity.CartItem;
import com.sa.event_mng.model.entity.TicketType;
import com.sa.event_mng.repository.CartItemRepository;
import com.sa.event_mng.repository.CartRepository;
import com.sa.event_mng.repository.TicketTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemSeeder implements CommandLineRunner {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    TicketTypeRepository ticketTypeRepository;
    Faker faker = new Faker();
    Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (cartItemRepository.count() == 0) {
            seedCartItems();
        }
    }

    private void seedCartItems() {
        List<Cart> carts = cartRepository.findAll();
        List<TicketType> ticketTypes = ticketTypeRepository.findAll();

        if (carts.isEmpty() || ticketTypes.isEmpty()) {
            return;
        }

        for (Cart cart : carts) {
            int numberOfItems = random.nextInt(3) + 1; // Mỗi giỏ có từ 1 đến 3 loại vé

            for (int i = 0; i < numberOfItems; i++) {
                TicketType randomTicketType = ticketTypes.get(random.nextInt(ticketTypes.size()));
                int quantity = random.nextInt(5) + 1;

                CartItem item = CartItem.builder()
                        .cart(cart)
                        .ticketType(randomTicketType)
                        .quantity(quantity)
                        .unitPrice(randomTicketType.getPrice())
                        .subtotal(randomTicketType.getPrice().multiply(BigDecimal.valueOf(quantity)))
                        .build();

                cartItemRepository.save(item);
            }
        }

        System.out.println("Seeded Cart Items.");
    }
}
*/

/*
package com.sa.event_mng.faker;

import com.github.javafaker.Faker;
import com.sa.event_mng.model.entity.Cart;
import com.sa.event_mng.model.entity.User;
import com.sa.event_mng.model.enums.CartStatus;
import com.sa.event_mng.repository.CartRepository;
import com.sa.event_mng.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartSeeder implements CommandLineRunner {

    CartRepository cartRepository;
    UserRepository userRepository;
    Faker faker = new Faker();
    Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (cartRepository.count() == 0) {
            seedCarts();
        }
    }

    private void seedCarts() {
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("CUSTOMER")))
                .toList();

        for (User customer : customers) {
            Cart cart = Cart.builder()
                    .customer(customer)
                    .status(CartStatus.ACTIVE)
                    .build();
            cartRepository.save(cart);
        }

        System.out.println("Seeded Carts.");
    }
}
*/

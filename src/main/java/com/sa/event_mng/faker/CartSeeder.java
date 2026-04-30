package com.sa.event_mng.faker;

import com.sa.event_mng.model.enums.CartStatus;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.ordering.domain.model.Cart;
import com.sa.event_mng.modules.ordering.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartSeeder {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public void seed() {
        List<User> customers = userRepository.findByRoles_Name("CUSTOMER");
        if (customers.isEmpty()) return;

        for (User customer : customers) {
            if (cartRepository.findByCustomerId(customer.getId()).isPresent()) continue;

            Cart cart = Cart.builder()
                    .customer(customer)
                    .status(CartStatus.ACTIVE)
                    .build();
            cartRepository.save(cart);
        }
    }
}

package com.sa.event_mng.faker;

import com.sa.event_mng.modules.identity.domain.model.Role;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.repository.RoleRepository;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    private static final int ORGANIZER_COUNT = 50;
    private static final int CUSTOMER_COUNT = 500;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        createUsersByRole("ORGANIZER", "organizer", "Organizer", ORGANIZER_COUNT);
        createStaff();
        createUsersByRole("CUSTOMER", "customer", "Customer", CUSTOMER_COUNT);
    }

    private void createStaff() {
        if (userRepository.existsByUsername("staff1")) {
            return;
        }

        Role staffRole = roleRepository.findByName("STAFF");
        Set<Role> roles = new HashSet<>();
        roles.add(staffRole);

        User staff = User.builder()
                .username("staff1")
                .password(passwordEncoder.encode("password"))
                .fullName("Staff One")
                .email("staff1@example.com")
                .enabled(true)
                .roles(roles)
                .build();
        userRepository.save(staff);
    }

    private void createUsersByRole(String roleName, String usernamePrefix, String fullNamePrefix, int count) {
        Role role = roleRepository.findByName(roleName);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        List<User> usersToSave = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String username = usernamePrefix + i;
            if (userRepository.existsByUsername(username)) {
                continue;
            }

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .fullName(fullNamePrefix + " " + i)
                    .email(username + "@example.com")
                    .enabled(true)
                    .roles(roles)
                    .build();
            usersToSave.add(user);
        }

        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
        }
    }
}

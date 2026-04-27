package com.sa.event_mng.faker;

import com.github.javafaker.Faker;
import com.sa.event_mng.model.entity.User;
import com.sa.event_mng.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
public class UserSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final com.sa.event_mng.repository.RoleRepository roleRepository;
    private final Faker faker = new Faker(new Locale("vi"));
    private final Random random = new Random();

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, com.sa.event_mng.repository.RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void seed() {
        if (userRepository.count() > 1) return;

        // Tạo sẵn các quyền cơ bản nếu DB chưa có
        List<com.sa.event_mng.model.entity.Role> dbRoles = roleRepository.findAll();
        if (dbRoles.isEmpty()) {
            dbRoles.add(roleRepository.save(com.sa.event_mng.model.entity.Role.builder().name("CUSTOMER").description("Khách hàng").build()));
            dbRoles.add(roleRepository.save(com.sa.event_mng.model.entity.Role.builder().name("ORGANIZER").description("Ban tổ chức").build()));
            dbRoles.add(roleRepository.save(com.sa.event_mng.model.entity.Role.builder().name("STAFF").description("Nhân viên").build()));
        }

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            User user = new User();

            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setFullName(faker.name().fullName());
            user.setPhone("09" + String.format("%08d", random.nextInt(100000000)));
            user.setAddress(faker.address().fullAddress());
            
            java.util.Set<com.sa.event_mng.model.entity.Role> roles = new java.util.HashSet<>();
            roles.add(dbRoles.get(random.nextInt(dbRoles.size())));
            user.setRoles(roles);
            
            user.setEnabled(true);
            user.setVerificationToken(null);

            users.add(user);
        }

        userRepository.saveAll(users);
        System.out.println("Seeded " + users.size() + " users");
    }
}

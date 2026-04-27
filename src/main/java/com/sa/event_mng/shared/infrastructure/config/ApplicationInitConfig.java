package com.sa.event_mng.shared.infrastructure.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.model.Role;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.identity.domain.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

  PasswordEncoder passwordEncoder;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring",
      value = "datasource.driver-class-name",
      havingValue = "com.mysql.cj.jdbc.Driver")
  ApplicationRunner applicationRunner(UserRepository userRepo, RoleRepository roleRepo) {
    log.info("CONFIG: Init Application");
    return args -> {
      if (userRepo.findByUsername("admin").isEmpty()) {
        User user =
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .fullName("ADMIN-MANAGERMENT")
                .email("admin@gmail.com")
                .phone("0123456789")
                .enabled(true)
                .build();
                
        // Lấy Role ADMIN từ DB, nếu chưa có thì lưu mới
        Role adminRole = roleRepo.findById("ADMIN").orElseGet(() -> {
            return roleRepo.save(Role.builder()
                .name("ADMIN")
                .description("Administrator")
                .build());
        });

        java.util.Set<Role> roles = new java.util.HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);

        userRepo.save(user);
        log.info("admin account has been created with default: (username,password) - (admin,admin) , please change it !");
      } else {
        log.info("Admin account already exists");
      }
    };
  }
}

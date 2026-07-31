package com.sofrecom.fleetmanagement.config;

import com.sofrecom.fleetmanagement.Repository.UserRepository;
import com.sofrecom.fleetmanagement.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthDataInitializer {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin1234";

    @Bean
    CommandLineRunner ensureDefaultAdmin(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User admin = userRepository.findByUsername(DEFAULT_ADMIN_USERNAME)
                    .orElseGet(User::new);

            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        };
    }
}

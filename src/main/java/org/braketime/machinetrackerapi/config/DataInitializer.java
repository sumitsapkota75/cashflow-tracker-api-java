package org.braketime.machinetrackerapi.config;


import org.braketime.machinetrackerapi.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.braketime.machinetrackerapi.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository) {
        return args -> {
            seedOwner("OWNER");
        };
    }

    public void seedOwner(String roleName){
        String ownerUsername = "owner";
        if (userRepository.findByUsername(ownerUsername).isPresent()){
            log.info(".....DEFAULT OWNER ALREADY EXISTS.....");
            return;
        }

        // create owner user:
        User owner = User.builder()
                .username("owner")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role("OWNER")
                .businessId(null)
                .active(true)
                .build();

        userRepository.save(owner);

        log.info("✅ Default OWNER user created");
        log.info("   Username    : owner");
        log.info("   Password : admin123");
    }
}

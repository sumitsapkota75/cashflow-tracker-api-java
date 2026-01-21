package org.braketime.machinetrackerapi.config;


import org.braketime.machinetrackerapi.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.braketime.machinetrackerapi.repository.RoleRepository;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("OWNER"));
                roleRepository.save(new Role("MANAGER"));
                roleRepository.save(new Role("EMPLOYEE"));
                log.info("✅ Default roles inserted");
            } else {
                log.info("ℹ️ Roles already exist");
            }
        };
    }
}

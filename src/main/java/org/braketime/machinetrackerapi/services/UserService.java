package org.braketime.machinetrackerapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.domain.Role;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.repository.RoleRepository;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterUserRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email already registered");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .role(request.getRole())
                .businessId(request.getBusinessId())
                .active(true)
                .build();
        return userRepository.save(user);
    }
}

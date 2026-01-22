package org.braketime.machinetrackerapi.services;

import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.domain.Role;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.repository.RoleRepository;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterUserRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }
        Role role = roleRepository.findByRole(request.getRole()).orElseThrow(()-> new RuntimeException("Invalid Role"));
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .roleId(role.getId())
                .businessId(request.getBusinessId())
                .isActive(true)
                .build();
        return userRepository.save(user);
    }
}

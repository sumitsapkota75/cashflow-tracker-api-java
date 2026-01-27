package org.braketime.machinetrackerapi.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.braketime.machinetrackerapi.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        Claims claims = jwtUtil.extractClaims(token);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("claims", claims);

        return response;


    }
}

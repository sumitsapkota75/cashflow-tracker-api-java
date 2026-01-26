package org.braketime.machinetrackerapi.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.JwtResponse;
import org.braketime.machinetrackerapi.Dtos.LoginRequest;
import org.braketime.machinetrackerapi.domain.Role;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.repository.RoleRepository;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.braketime.machinetrackerapi.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController{
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request){
    log.info("LOGIN API HIT");
    log.info(request.getEmail());
    log.info(request.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            throw e; // or return custom response
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String token = jwtUtil.generateToken(user.getEmail(),user.getId(), user.getRole());
        return new JwtResponse(token,user.getRole());
    }
}
package org.braketime.machinetrackerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.Dtos.UserCreateResponse;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.mapper.UserResponseMapper;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.braketime.machinetrackerapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Create user api hit");
        User user = userService.createUser(request);
        return ResponseEntity.ok(
                userResponseMapper.toResponse(user)
        );
    }

    @GetMapping
    public ResponseEntity<?> getUsers(){
        log.info("Get users api hit");
        List<User> users = userRepository.getAllByIsActive(true);
        List<UserCreateResponse> response = users.stream()
                .map(userResponseMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable String id
    ){
        return userRepository.findById(id)
                .map(userResponseMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

}

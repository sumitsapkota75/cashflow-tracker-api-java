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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return ResponseEntity.ok(
                userService.createUser(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<?>> getAllUsers(Pageable pageable){
        log.info("Get users api hit");

        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable String id
    ){
        return ResponseEntity.ok(userService.getUser(id));
    }

}

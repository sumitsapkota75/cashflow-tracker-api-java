package org.braketime.machinetrackerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody RegisterUserRequest request
    ){
        return  ResponseEntity.ok(userService.updateUser(request, id));
    }

}

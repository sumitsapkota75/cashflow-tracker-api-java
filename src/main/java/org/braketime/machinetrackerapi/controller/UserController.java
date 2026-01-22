package org.braketime.machinetrackerapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.mapper.UserResponseMapper;
import org.braketime.machinetrackerapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUserRequest request){
        try {
            log.info("Create user api hit");
        User createdUser = userService.createUser(request);
        return ResponseEntity.ok(userResponseMapper.toResponse(createdUser));
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}

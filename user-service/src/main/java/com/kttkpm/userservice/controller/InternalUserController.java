package com.kttkpm.userservice.controller;

import com.kttkpm.userservice.dto.response.UserValidationResponse;
import com.kttkpm.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/validation")
    public UserValidationResponse validateUser(@PathVariable String id) {
        return userService.validateUser(id);
    }
}

package com.johnmartin.coaching.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.dto.response.common.PagedResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.service.UserService;

import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(ApiConstants.Path.API_USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Result<PagedResponse<UserResponse>>> getUsers(@RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        PagedResponse<UserResponse> users = userService.getUsers(page);
        return ResponseEntity.ok(Result.success(users));
    }
}

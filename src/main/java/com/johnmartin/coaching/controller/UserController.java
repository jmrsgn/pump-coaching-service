package com.johnmartin.coaching.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.request.CreateClientUserRequest;
import com.johnmartin.coaching.dto.response.ClientUserResponse;
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
    public ResponseEntity<Result<PagedResponse<ClientUserResponse>>> getUsers(@RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        PagedResponse<ClientUserResponse> users = userService.getUsers(page);
        return ResponseEntity.ok(Result.success(users));
    }

    @PostMapping(ApiConstants.Path.CREATE_USER)
    public ResponseEntity<Result<Void>> createUser(@RequestBody CreateClientUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok(Result.success(null));
    }
}

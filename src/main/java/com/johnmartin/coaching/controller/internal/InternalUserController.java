package com.johnmartin.coaching.controller.internal;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.request.CreateUserRequest;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(ApiConstants.InternalPath.API_USER_INTERNAL)
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(ApiConstants.InternalPath.CREATE_USER)
    public ResponseEntity<Result<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(Result.success(user));
    }
}

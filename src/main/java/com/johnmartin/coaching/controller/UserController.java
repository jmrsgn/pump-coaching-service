package com.johnmartin.coaching.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.dto.response.internal.SocialUserSummaryResponse;
import com.johnmartin.coaching.service.UserService;

@RestController
@RequestMapping(ApiConstants.Path.API_USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(ApiConstants.Path.SEARCH_USER)
    public ResponseEntity<Result<List<SocialUserSummaryResponse>>> searchUsers(@RequestParam(ApiConstants.Params.QUERY) String query) {
        List<SocialUserSummaryResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(Result.success(users));
    }
}

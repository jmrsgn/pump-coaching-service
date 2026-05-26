package com.johnmartin.coaching.mapper;

import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.internal.AuthUserResponse;

public class UserMapper {

    private UserMapper() {
    }

    public static AuthUser toAuthUser(AuthUserResponse authUserResponse) {
        return new AuthUser(authUserResponse.id(),
                            authUserResponse.firstName(),
                            authUserResponse.lastName(),
                            authUserResponse.email(),
                            authUserResponse.phone());
    }
}

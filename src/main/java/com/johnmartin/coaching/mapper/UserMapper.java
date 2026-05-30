package com.johnmartin.coaching.mapper;

import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.internal.AuthUserResponse;
import com.johnmartin.coaching.dto.response.ClientUserResponse;
import com.johnmartin.coaching.entity.ClientProfileEntity;

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

    public static ClientUserResponse toResponse(ClientProfileEntity clientProfileEntity) {
        return new ClientUserResponse();
    }
}

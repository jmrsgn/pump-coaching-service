package com.johnmartin.coaching.mapper;

import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.internal.AuthUserResponse;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.entity.UserEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(UserEntity userEntity) {
        return new UserResponse(userEntity.getId(),
                                userEntity.getCoachId(),
                                userEntity.getFirstName(),
                                userEntity.getLastName(),
                                userEntity.getProfileImageUrl(),
                                userEntity.getGender(),
                                userEntity.getBirthDate(),
                                userEntity.getHeightCm(),
                                userEntity.getCurrentWeight(),
                                userEntity.getGoalWeight(),
                                userEntity.getActivityLevel(),
                                userEntity.getFitnessGoal(),
                                userEntity.getCreatedAt(),
                                userEntity.getUpdatedAt());
    }

    public static AuthUser toAuthUser(AuthUserResponse authUserResponse) {
        return new AuthUser(authUserResponse.id(),
                            authUserResponse.firstName(),
                            authUserResponse.lastName(),
                            authUserResponse.email(),
                            authUserResponse.phone());
    }
}

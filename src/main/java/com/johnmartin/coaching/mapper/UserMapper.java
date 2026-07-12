package com.johnmartin.coaching.mapper;

import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.response.ClientUserResponse;
import com.johnmartin.coaching.dto.response.internal.AuthUserResponse;
import com.johnmartin.coaching.dto.response.internal.SocialUserSummaryResponse;
import com.johnmartin.coaching.entity.ClientProfileEntity;
import com.johnmartin.coaching.enums.CoachingStatus;

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

    public static ClientUserResponse toClientUserResponse(ClientProfileEntity profile,
                                                          SocialUserSummaryResponse socialUser,
                                                          CoachingStatus status) {
        return new ClientUserResponse(profile.getUserId(),
                                      socialUser.firstName(),
                                      socialUser.lastName(),
                                      socialUser.profileImageUrl(),
                                      profile.getGender(),
                                      profile.getAge(),
                                      profile.getHeightCm(),
                                      profile.getCurrentWeight(),
                                      profile.getGoalWeight(),
                                      profile.getActivityLevel(),
                                      profile.getFitnessGoal(),
                                      profile.getCreatedAt(),
                                      profile.getUpdatedAt(),
                                      status);
    }
}

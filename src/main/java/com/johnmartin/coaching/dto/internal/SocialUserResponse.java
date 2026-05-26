package com.johnmartin.coaching.dto.internal;

public record SocialUserResponse(String id,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String profileImageUrl,
                                 String bio,

                                 long followersNo,
                                 long followingNo,
                                 boolean isFollowing) {
}

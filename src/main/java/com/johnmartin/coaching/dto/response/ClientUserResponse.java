package com.johnmartin.coaching.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.johnmartin.coaching.enums.ActivityLevel;
import com.johnmartin.coaching.enums.CoachingStatus;
import com.johnmartin.coaching.enums.FitnessGoal;
import com.johnmartin.coaching.enums.Gender;

public record ClientUserResponse(UUID id,
                                 String firstName,
                                 String lastName,
                                 String profileImageUrl,

                                 Gender gender,
                                 Integer age,

                                 Double heightCm,
                                 Double currentWeight,
                                 Double goalWeight,

                                 ActivityLevel activityLevel,
                                 FitnessGoal fitnessGoal,

                                 Instant createdAt,
                                 Instant updatedAt,

                                 CoachingStatus status) {

}

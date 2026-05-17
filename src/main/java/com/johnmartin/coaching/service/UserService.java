package com.johnmartin.coaching.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.johnmartin.coaching.dto.request.CreateUserRequest;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.entity.UserEntity;
import com.johnmartin.coaching.mapper.UserMapper;
import com.johnmartin.coaching.repository.UserRepository;
import com.johnmartin.coaching.utilities.LoggerUtility;

@Service
public class UserService {

    private static final Class<UserService> clazz = UserService.class;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        LoggerUtility.d(clazz, "Execute method: [createUser]");

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setProfileImageUrl(request.profileImageUrl());
        user.setGender(request.gender());
        user.setBirthDate(request.birthDate());
        user.setHeightCm(request.heightCm());
        user.setCurrentWeight(request.currentWeight());
        user.setGoalWeight(request.goalWeight());
        user.setActivityLevel(request.activityLevel());
        user.setFitnessGoal(request.fitnessGoal());

        UserEntity createdUser = userRepository.save(user);
        return UserMapper.toResponse(createdUser);
    }
}

package com.johnmartin.coaching.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.johnmartin.coaching.constants.UIConstants;
import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.request.CreateUserRequest;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.dto.response.common.PagedResponse;
import com.johnmartin.coaching.entity.UserEntity;
import com.johnmartin.coaching.mapper.UserMapper;
import com.johnmartin.coaching.repository.UserRepository;
import com.johnmartin.coaching.utilities.LoggerUtility;

@Service
public class UserService {

    private static final Class<UserService> clazz = UserService.class;
    private final UserRepository userRepository;

    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserResponse createUser(CreateUserRequest request) {
        LoggerUtility.d(clazz, "Execute method: [createUser]");
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setCoachId(request.coachId());
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

    /**
     * Get clients under a coach
     * 
     * @param page
     *            - page
     * @return List of UserResponse
     */
    public PagedResponse<UserResponse> getUsers(int page) {
        LoggerUtility.d(clazz, String.format("Execute method: [getUsers], page: [%d]", page));

        // Get authenticated coach
        AuthUser authUser = authService.getAuthUser();

        // Build pagination
        PageRequest pageRequest = PageRequest.of(page, UIConstants.MINIMUM_USERS);
        UUID coachId = UUID.fromString(authUser.id());
        Page<UserEntity> usersPage = userRepository.findByCoachId(coachId, pageRequest);
        List<UserResponse> users = usersPage.getContent().stream().map(UserMapper::toResponse).toList();
        LoggerUtility.d(clazz, String.format("users size: [%d]", users.size()));

        return new PagedResponse<>(users,
                                   usersPage.getNumber(),
                                   usersPage.getSize(),
                                   usersPage.getTotalElements(),
                                   usersPage.getTotalPages(),
                                   usersPage.hasNext());
    }
}

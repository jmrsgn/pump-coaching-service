package com.johnmartin.coaching.service;

import java.util.UUID;

import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import com.johnmartin.coaching.constants.SecurityConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;
import com.johnmartin.coaching.constants.error.domain.UserErrorConstants;
import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.internal.SocialUserResponse;
import com.johnmartin.coaching.dto.request.CreateClientUserRequest;
import com.johnmartin.coaching.dto.response.UserResponse;
import com.johnmartin.coaching.dto.response.common.PagedResponse;
import com.johnmartin.coaching.entity.ClientProfileEntity;
import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;
import com.johnmartin.coaching.enums.CoachingStatus;
import com.johnmartin.coaching.exceptions.BadRequestException;
import com.johnmartin.coaching.exceptions.ConflictException;
import com.johnmartin.coaching.repository.ClientProfileRepository;
import com.johnmartin.coaching.repository.CoachClientRelationshipRepository;
import com.johnmartin.coaching.service.internal.client.SocialServiceClient;
import com.johnmartin.coaching.utilities.LoggerUtility;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private static final Class<UserService> clazz = UserService.class;
    private final ClientProfileRepository clientProfileRepository;
    private final CoachClientRelationshipRepository coachClientRelationshipRepository;

    private final AuthService authService;

    private final SocialServiceClient socialServiceClient;

    public UserService(ClientProfileRepository clientProfileRepository,
                       CoachClientRelationshipRepository coachClientRelationshipRepository,
                       AuthService authService,
                       SocialServiceClient socialServiceClient) {
        this.clientProfileRepository = clientProfileRepository;
        this.coachClientRelationshipRepository = coachClientRelationshipRepository;
        this.authService = authService;
        this.socialServiceClient = socialServiceClient;
    }

    @Transactional
    public void createUser(CreateClientUserRequest request) {
        LoggerUtility.d(clazz, "Execute method: [createUser]");

        if (request == null) {
            throw new BadRequestException(SystemErrorConstants.INVALID_REQUEST);
        }

        // Get authenticated coach
        AuthUser authUser = authService.getAuthUser();

        String requestId = (String) MDC.get(SecurityConstants.HttpHeaders.REQUEST_ID);
        LoggerUtility.d(clazz, String.format("requestId: [%s]", requestId));

        // Validate if target user exists
        SocialUserResponse socialUser = socialServiceClient.getSocialUserById(request.userId(), requestId);
        UUID clientUserId = UUID.fromString(socialUser.id());

        // Prevent duplicate client profile
        if (clientProfileRepository.existsByUserId(clientUserId)) {
            throw new ConflictException(UserErrorConstants.CLIENT_PROFILE_ALREADY_EXISTS);
        }

        // Prevent duplicate coach-client relationship
        if (coachClientRelationshipRepository.existsByCoachIdAndClientId(UUID.fromString(authUser.id()),
                                                                         clientUserId)) {
            throw new ConflictException(UserErrorConstants.CLIENT_IS_ALREADY_ENROLLED);
        }

        // Create client profile
        ClientProfileEntity profile = new ClientProfileEntity();
        profile.setUserId(clientUserId);
        profile.setGender(request.gender());
        profile.setBirthDate(request.birthDate());
        profile.setHeightCm(request.heightCm());
        profile.setCurrentWeight(request.currentWeight());
        profile.setGoalWeight(request.goalWeight());
        profile.setActivityLevel(request.activityLevel());
        profile.setFitnessGoal(request.fitnessGoal());
        clientProfileRepository.save(profile);

        // Create coach-client relationship
        CoachClientRelationshipEntity relationship = new CoachClientRelationshipEntity();
        relationship.setCoachId(UUID.fromString(authUser.id()));
        relationship.setClientId(clientUserId);
        relationship.setStatus(CoachingStatus.ACTIVE.getValue());
        coachClientRelationshipRepository.save(relationship);
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
        // AuthUser authUser = authService.getAuthUser();
        //
        // // Build pagination
        // PageRequest pageRequest = PageRequest.of(page, UIConstants.MINIMUM_USERS);
        // UUID coachId = UUID.fromString(authUser.id());
        // Page<ClientProfileEntity> usersPage = clientProfileRepository.findByCoachId(coachId, pageRequest);
        // List<UserResponse> users = usersPage.getContent().stream().map(UserMapper::toResponse).toList();
        // LoggerUtility.d(clazz, String.format("users size: [%d]", users.size()));

        // return new PagedResponse<>(users,
        // usersPage.getNumber(),
        // usersPage.getSize(),
        // usersPage.getTotalElements(),
        // usersPage.getTotalPages(),
        // usersPage.hasNext());

        return null;
    }
}

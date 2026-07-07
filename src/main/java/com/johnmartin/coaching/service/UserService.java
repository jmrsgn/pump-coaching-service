package com.johnmartin.coaching.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.johnmartin.coaching.constants.SecurityConstants;
import com.johnmartin.coaching.constants.UIConstants;
import com.johnmartin.coaching.constants.error.SystemErrorConstants;
import com.johnmartin.coaching.constants.error.domain.UserErrorConstants;
import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.dto.request.CreateClientUserRequest;
import com.johnmartin.coaching.dto.response.ClientUserResponse;
import com.johnmartin.coaching.dto.response.common.PagedResponse;
import com.johnmartin.coaching.dto.response.internal.SocialUserResponse;
import com.johnmartin.coaching.dto.response.internal.SocialUserSummaryResponse;
import com.johnmartin.coaching.entity.ClientProfileEntity;
import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;
import com.johnmartin.coaching.enums.CoachingStatus;
import com.johnmartin.coaching.exceptions.BadRequestException;
import com.johnmartin.coaching.exceptions.ConflictException;
import com.johnmartin.coaching.mapper.UserMapper;
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
    public PagedResponse<ClientUserResponse> getUsers(int page) {
        LoggerUtility.d(clazz, String.format("Execute method: [getUsers], page: [%d]", page));

        // Get authenticated coach
        AuthUser authUser = authService.getAuthUser();

        String requestId = MDC.get(SecurityConstants.HttpHeaders.REQUEST_ID).toString();

        // Build pagination
        UUID coachId = UUID.fromString(authUser.id());
        PageRequest pageRequest = PageRequest.of(page, UIConstants.MINIMUM_USERS);
        Page<ClientProfileEntity> profilesPage = clientProfileRepository.findByCoachId(coachId, pageRequest);
        List<ClientProfileEntity> profiles = profilesPage.getContent();
        LoggerUtility.logItemSize(clazz, "profiles", profiles);

        // Extract userIds
        List<String> userIds = profiles.stream().map(profile -> profile.getUserId().toString()).toList();

        // Batch fetch social users
        List<SocialUserSummaryResponse> socialUsers = socialServiceClient.getUsersByIds(authUser.id(),
                                                                                        userIds,
                                                                                        requestId);
        LoggerUtility.logItemSize(clazz, "socialUsers", socialUsers);

        // Fast lookup map
        Map<String, SocialUserSummaryResponse> socialUsersMap = socialUsers.stream()
                                                                           .collect(Collectors.toMap(SocialUserSummaryResponse::id,
                                                                                                     Function.identity()));

        // Merge profile + social user
        List<ClientUserResponse> users = profiles.stream().map(profile -> {
            SocialUserSummaryResponse socialUser = socialUsersMap.get(profile.getUserId().toString());
            return UserMapper.toResponse(profile, socialUser);
        }).toList();

        LoggerUtility.logItemSize(clazz, "users", users);

        return new PagedResponse<>(users,
                                   profilesPage.getNumber(),
                                   profilesPage.getSize(),
                                   profilesPage.getTotalElements(),
                                   profilesPage.getTotalPages(),
                                   profilesPage.hasNext());
    }

    @Transactional
    public void deleteClientProfile(UUID userId) {
        LoggerUtility.d(clazz, String.format("Execute method: [deleteClientProfile] userId: [%s]", userId.toString()));

        // Delete first the relationship
        coachClientRelationshipRepository.deleteByClientId(userId);

        // Delete client profile
        clientProfileRepository.deleteByUserId(userId);
    }

    /**
     * Search users based on query
     * 
     * @param query
     *            - query
     * @return List<SocialUserSummaryResponse>
     */
    public List<SocialUserSummaryResponse> searchUsers(String query) {
        LoggerUtility.d(clazz, String.format("Execute method: [searchUsers] query: [%s]", query));

        if (StringUtils.isBlank(query)) {
            return Collections.emptyList();
        }

        AuthUser authUser = authService.getAuthUser();

        String requestId = (String) MDC.get(SecurityConstants.HttpHeaders.REQUEST_ID);

        List<SocialUserSummaryResponse> users = socialServiceClient.searchUsers(authUser.id(), query.trim(), requestId);
        LoggerUtility.logItemSize(clazz, "users", users);

        UUID coachId = UUID.fromString(authUser.id());

        List<UUID> enrolledIds = coachClientRelationshipRepository.findByCoachId(coachId)
                                                                  .stream()
                                                                  .map(CoachClientRelationshipEntity::getClientId)
                                                                  .toList();
        LoggerUtility.logItemSize(clazz, "enrolledIds", enrolledIds);

        // Filter users that are not currently enrolled from coach
        List<SocialUserSummaryResponse> filteredUsers = users.stream()
                                                             .filter(user -> !enrolledIds.contains(UUID.fromString(user.id())))
                                                             .toList();

        LoggerUtility.logItemSize(clazz, "filteredUsers", filteredUsers);
        return filteredUsers;
    }
}

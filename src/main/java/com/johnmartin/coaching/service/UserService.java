package com.johnmartin.coaching.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.TrainingBlockStatus;
import com.johnmartin.coaching.exceptions.NotFoundException;
import com.johnmartin.coaching.repository.TrainingBlockRepository;
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
    private final TrainingBlockRepository trainingBlockRepository;

    private final AuthService authService;

    private final SocialServiceClient socialServiceClient;

    public UserService(ClientProfileRepository clientProfileRepository,
                       CoachClientRelationshipRepository coachClientRelationshipRepository,
                       TrainingBlockRepository trainingBlockRepository,
                       AuthService authService,
                       SocialServiceClient socialServiceClient) {
        this.clientProfileRepository = clientProfileRepository;
        this.coachClientRelationshipRepository = coachClientRelationshipRepository;
        this.trainingBlockRepository = trainingBlockRepository;
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
        SocialUserResponse socialUser = socialServiceClient.getSocialUserById(authUser.id(),
                                                                              request.userId(),
                                                                              requestId);
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
        profile.setAge(request.age());
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

    public PagedResponse<ClientUserResponse> getUsers(int page) {
        LoggerUtility.d(clazz, String.format("Execute method: [getUsers], page: [%d]", page));

        // Get authenticated coach.
        AuthUser authUser = authService.getAuthUser();

        UUID coachId = UUID.fromString(authUser.id());

        String requestId = MDC.get(SecurityConstants.HttpHeaders.REQUEST_ID).toString();

        // Get paginated client profiles under authenticated coach.
        PageRequest pageRequest = PageRequest.of(page, UIConstants.MINIMUM_USERS);

        Page<ClientProfileEntity> profilesPage = clientProfileRepository.findByCoachId(coachId, pageRequest);

        List<ClientProfileEntity> profiles = profilesPage.getContent();

        LoggerUtility.logItemSize(clazz, "profiles", profiles);

        // Nothing else needs to be fetched when the page is empty.
        if (profiles.isEmpty()) {
            return new PagedResponse<>(List.of(),
                                       profilesPage.getNumber(),
                                       profilesPage.getSize(),
                                       profilesPage.getTotalElements(),
                                       profilesPage.getTotalPages(),
                                       profilesPage.hasNext());
        }

        // Extract client IDs once and reuse them.
        List<UUID> clientIds = profiles.stream().map(ClientProfileEntity::getUserId).toList();

        List<String> clientIdStrings = clientIds.stream().map(UUID::toString).toList();

        // Batch fetch social users.
        List<SocialUserSummaryResponse> socialUsers = socialServiceClient.getUsersByIds(authUser.id(),
                                                                                        clientIdStrings,
                                                                                        requestId);

        LoggerUtility.logItemSize(clazz, "socialUsers", socialUsers);

        Map<String, SocialUserSummaryResponse> socialUsersMap = socialUsers.stream()
                                                                           .collect(Collectors.toMap(SocialUserSummaryResponse::id,
                                                                                                     Function.identity()));

        // Batch fetch coach-client relationships.
        List<CoachClientRelationshipEntity> relationships = coachClientRelationshipRepository.findByCoachIdAndClientIdIn(coachId,
                                                                                                                         clientIdStrings);

        Map<UUID, CoachClientRelationshipEntity> relationshipMap = relationships.stream()
                                                                                .collect(Collectors.toMap(CoachClientRelationshipEntity::getClientId,
                                                                                                          Function.identity()));

        // Batch fetch active training blocks.
        List<TrainingBlockEntity> activeTrainingBlocks = trainingBlockRepository.findByCoachIdAndClientIdInAndStatus(coachId,
                                                                                                                     clientIds,
                                                                                                                     TrainingBlockStatus.ACTIVE);

        Set<UUID> clientsWithActiveTrainingBlock = activeTrainingBlocks.stream()
                                                                       .map(TrainingBlockEntity::getClientId)
                                                                       .collect(Collectors.toSet());

        // Merge profile + social + relationship + training block state.
        List<ClientUserResponse> users = profiles.stream().map(profile -> {
            UUID clientId = profile.getUserId();

            SocialUserSummaryResponse socialUser = socialUsersMap.get(clientId.toString());

            CoachClientRelationshipEntity relationship = relationshipMap.get(clientId);

            boolean hasActiveTrainingBlock = clientsWithActiveTrainingBlock.contains(clientId);

            return UserMapper.toClientUserResponse(profile,
                                                   socialUser,
                                                   CoachingStatus.fromCode(relationship.getStatus()),
                                                   hasActiveTrainingBlock);
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

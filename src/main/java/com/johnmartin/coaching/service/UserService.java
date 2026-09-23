package com.johnmartin.coaching.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.TrainingBlockStatus;
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
    private final CoachClientRelationshipRepository coachClientRelationshipRepository;

    private final AuthService authService;

    private final SocialServiceClient socialServiceClient;

    public UserService(CoachClientRelationshipRepository coachClientRelationshipRepository,
                       AuthService authService,
                       SocialServiceClient socialServiceClient) {
        this.coachClientRelationshipRepository = coachClientRelationshipRepository;
        this.authService = authService;
        this.socialServiceClient = socialServiceClient;
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

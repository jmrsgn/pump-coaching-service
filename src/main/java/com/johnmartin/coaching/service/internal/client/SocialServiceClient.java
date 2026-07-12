package com.johnmartin.coaching.service.internal.client;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.johnmartin.coaching.constants.SecurityConstants;
import com.johnmartin.coaching.constants.UIConstants;
import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.constants.api.ExternalServiceConstants;
import com.johnmartin.coaching.constants.error.ExternalServiceErrorConstants;
import com.johnmartin.coaching.dto.request.internal.GetSocialUsersRequest;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.dto.response.internal.SocialUserResponse;
import com.johnmartin.coaching.dto.response.internal.SocialUserSummaryResponse;
import com.johnmartin.coaching.exceptions.NotFoundException;
import com.johnmartin.coaching.utilities.LoggerUtility;

@Service
public class SocialServiceClient {

    private static final Class<SocialServiceClient> clazz = SocialServiceClient.class;

    private final RestClient socialServiceRestClient;

    private final String internalServiceToken;

    public SocialServiceClient(RestClient socialServiceRestClient,
                               @Value("${pump.security.internal-service-token}") String internalServiceToken) {
        this.socialServiceRestClient = socialServiceRestClient;
        this.internalServiceToken = internalServiceToken;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public SocialUserResponse getSocialUserById(String currentUserId, String userId, String requestId) {
        try {
            Result<SocialUserResponse> result = socialServiceRestClient.get()
                                                                       .uri(ExternalServiceConstants.PumpSocialService.API_USERS
                                                                            + "/" + userId)
                                                                       .header(HttpHeaders.AUTHORIZATION,
                                                                               SecurityConstants.HttpHeaders.BEARER
                                                                                                          + internalServiceToken)
                                                                       .header(SecurityConstants.HttpHeaders.USER_ID,
                                                                               currentUserId)
                                                                       .header(SecurityConstants.HttpHeaders.REQUEST_ID,
                                                                               requestId)
                                                                       .retrieve()
                                                                       .body(new ParameterizedTypeReference<>() {
                                                                       });

            if (result == null || result.getData().isEmpty()) {
                throw new RuntimeException(ExternalServiceErrorConstants.SOCIAL_USER_NOT_FOUND);
            }

            return result.getData().get();
        } catch (HttpClientErrorException ex) {
            throw new NotFoundException(ExternalServiceErrorConstants.SOCIAL_USER_NOT_FOUND);
        } catch (Exception ex) {
            throw new RuntimeException(ExternalServiceErrorConstants.FAILED_TO_GET_SOCIAL_USER);
        }
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public List<SocialUserSummaryResponse> getUsersByIds(String currentUserId, List<String> userIds, String requestId) {
        try {
            Result<List<SocialUserSummaryResponse>> result = socialServiceRestClient.post()
                                                                                    .uri(ExternalServiceConstants.PumpSocialService.API_USERS)
                                                                                    .header(HttpHeaders.AUTHORIZATION,
                                                                                            SecurityConstants.HttpHeaders.BEARER
                                                                                                                       + internalServiceToken)
                                                                                    .header(SecurityConstants.HttpHeaders.USER_ID,
                                                                                            currentUserId)
                                                                                    .header(SecurityConstants.HttpHeaders.REQUEST_ID,
                                                                                            requestId)
                                                                                    .body(new GetSocialUsersRequest(userIds))
                                                                                    .retrieve()
                                                                                    .body(new ParameterizedTypeReference<>() {
                                                                                    });

            if (result == null || result.getData().isEmpty()) {
                throw new RuntimeException(ExternalServiceErrorConstants.SOCIAL_USERS_NOT_FOUND);
            }

            return result.getData().get();
        } catch (HttpClientErrorException ex) {
            throw new NotFoundException(ExternalServiceErrorConstants.SOCIAL_USERS_NOT_FOUND);
        } catch (Exception ex) {
            throw new RuntimeException(ExternalServiceErrorConstants.FAILED_TO_GET_SOCIAL_USERS);
        }
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public List<SocialUserSummaryResponse> searchUsers(String currentUserId, String query, String requestId) {
        try {
            Result<List<SocialUserSummaryResponse>> result = socialServiceRestClient.get()
                                                                                    .uri(uriBuilder -> uriBuilder.path(ExternalServiceConstants.PumpSocialService.API_SEARCH_USER)
                                                                                                                 .queryParam(ApiConstants.Params.QUERY,
                                                                                                                             query)
                                                                                                                 .build())
                                                                                    .header(HttpHeaders.AUTHORIZATION,
                                                                                            SecurityConstants.HttpHeaders.BEARER
                                                                                                                       + internalServiceToken)
                                                                                    .header(SecurityConstants.HttpHeaders.USER_ID,
                                                                                            currentUserId)
                                                                                    .header(SecurityConstants.HttpHeaders.REQUEST_ID,
                                                                                            requestId)
                                                                                    .retrieve()
                                                                                    .body(new ParameterizedTypeReference<>() {
                                                                                    });

            if (result == null || result.getData().isEmpty()) {
                LoggerUtility.d(clazz, "Result is empty, will return empty list");
                return Collections.emptyList();
            }

            return result.getData().get();
        } catch (Exception ex) {
            throw new RuntimeException(ExternalServiceErrorConstants.FAILED_TO_GET_SOCIAL_USERS);
        }
    }
}

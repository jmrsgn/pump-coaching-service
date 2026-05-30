package com.johnmartin.coaching.service.internal.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
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

@Service
public class SocialServiceClient {

    private final RestClient socialServiceRestClient;

    public SocialServiceClient(RestClient socialServiceRestClient) {
        this.socialServiceRestClient = socialServiceRestClient;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public SocialUserResponse getSocialUserById(String userId, String requestId) {
        try {
            Result<SocialUserResponse> result = socialServiceRestClient.get()
                                                                       .uri(ExternalServiceConstants.PumpSocialService.API_USER_INTERNAL
                                                                            + "/" + userId)
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
    public List<SocialUserSummaryResponse> getUsersByIds(List<String> userIds, String requestId) {
        try {
            Result<List<SocialUserSummaryResponse>> result = socialServiceRestClient.post()
                                                                                    .uri(ExternalServiceConstants.PumpSocialService.API_USER_INTERNAL)
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
}

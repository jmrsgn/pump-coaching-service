package com.johnmartin.coaching.service.internal.client;

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
import com.johnmartin.coaching.dto.internal.SocialUserResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.exceptions.NotFoundException;

@Service
public class SocialServiceClient {

    private final RestClient socialServiceWebClient;

    public SocialServiceClient(RestClient socialServiceWebClient) {
        this.socialServiceWebClient = socialServiceWebClient;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public SocialUserResponse getSocialUserById(String userId, String requestId) {
        try {
            Result<SocialUserResponse> result = socialServiceWebClient.get()
                                                                      .uri(ExternalServiceConstants.PumpSocialService.API_GET_USER
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
}

package com.johnmartin.coaching.service.internal.client;

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
import com.johnmartin.coaching.constants.error.AuthErrorConstants;
import com.johnmartin.coaching.constants.error.ExternalServiceErrorConstants;
import com.johnmartin.coaching.dto.internal.AuthUserResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.exceptions.UnauthorizedException;

@Service
public class AuthServiceClient {

    private final RestClient authServiceRestClient;

    public AuthServiceClient(RestClient authServiceRestClient) {
        this.authServiceRestClient = authServiceRestClient;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = ApiConstants.RETRIES_COUNT, backoff = @Backoff(delay = UIConstants.DELAY_2000))
    public AuthUserResponse validate(String authorizationHeader, String requestId) {
        try {
            Result<AuthUserResponse> result = authServiceRestClient.post()
                                                                   .uri(ExternalServiceConstants.PumpAuthService.API_VALIDATE)
                                                                   .header(HttpHeaders.AUTHORIZATION,
                                                                           authorizationHeader)
                                                                   .header(SecurityConstants.HttpHeaders.REQUEST_ID,
                                                                           requestId)
                                                                   .retrieve()
                                                                   .body(new ParameterizedTypeReference<>() {
                                                                   });

            if (result == null || result.getData().isEmpty()) {
                throw new RuntimeException(ExternalServiceErrorConstants.AUTH_USER_NOT_FOUND);
            }

            return result.getData().get();
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new UnauthorizedException(AuthErrorConstants.USER_IS_NOT_AUTHENTICATED_OR_INVALID_TOKEN);
        } catch (Exception ex) {
            throw new RuntimeException(ExternalServiceErrorConstants.FAILED_TO_VALIDATE_CURRENT_USER);
        }
    }
}

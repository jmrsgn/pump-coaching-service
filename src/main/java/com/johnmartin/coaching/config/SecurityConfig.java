package com.johnmartin.coaching.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.security.custom.CustomAccessDeniedHandler;
import com.johnmartin.coaching.security.custom.CustomAuthEntryPoint;
import com.johnmartin.coaching.security.filter.AuthContextFilter;
import com.johnmartin.coaching.security.filter.CorrelationIdFilter;
import com.johnmartin.coaching.security.filter.RequestLoggingFilter;
import com.johnmartin.coaching.service.internal.client.AuthServiceClient;

@Configuration
public class SecurityConfig {

    @Bean
    public CorrelationIdFilter correlationIdFilter(ObjectMapper objectMapper) {
        return new CorrelationIdFilter(objectMapper);
    }

    @Bean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    public AuthContextFilter authContextFilter(AuthServiceClient authServiceClient,
                                               ObjectMapper objectMapper,
                                               @Value("${pump.security.internal-service-token}") String internalServiceToken) {
        return new AuthContextFilter(authServiceClient, objectMapper, internalServiceToken);
    }

    /**
     * A filter that exposes the health endpoint for status checking
     *
     * @param http
     *            - HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception-
     *             Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthContextFilter authContextFilter,
                                                   CorrelationIdFilter correlationIdFilter,
                                                   RequestLoggingFilter requestLoggingFilter,
                                                   CustomAuthEntryPoint customAuthEntryPoint,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(authContextFilter, CorrelationIdFilter.class)
            .addFilterAfter(requestLoggingFilter, AuthContextFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthEntryPoint)
                                       .accessDeniedHandler(customAccessDeniedHandler))
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(ApiConstants.Path.ACTUATOR
                                                                          + ApiConstants.Path.HEALTH,
                                                                          ApiConstants.Path.ACTUATOR + ApiConstants.Path.HEALTH
                                                                                                      + "/**")
                                                         .permitAll()
                                                         .requestMatchers(ApiConstants.Path.ACTUATOR + "/**")
                                                         .denyAll()
                                                         .anyRequest()
                                                         .authenticated());
        return http.build();
    }
}

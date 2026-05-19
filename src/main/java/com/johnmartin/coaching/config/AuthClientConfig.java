package com.johnmartin.coaching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthClientConfig {

    @Bean
    public RestClient authWebClient() {
        // return RestClient.builder().baseUrl(ApiConstants.PumpAuthService.URL).build();
        return RestClient.builder().baseUrl("http://localhost:8081").build();
    }
}

package com.johnmartin.coaching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SocialServiceClientConfig {

    @Bean
    public RestClient socialServiceRestClient() {
        // return RestClient.builder().baseUrl(ApiConstants.PumpSocialService.URL).build();
        return RestClient.builder().baseUrl("http://localhost:8080").build();
    }
}

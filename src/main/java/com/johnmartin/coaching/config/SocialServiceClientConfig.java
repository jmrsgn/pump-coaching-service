package com.johnmartin.coaching.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.johnmartin.coaching.properties.ExternalServicesProperties;

@Configuration
@EnableConfigurationProperties(ExternalServicesProperties.class)
public class SocialServiceClientConfig {

    @Bean
    public RestClient socialServiceRestClient(ExternalServicesProperties properties) {
        return RestClient.builder().baseUrl(properties.getSocial().getUrl()).build();
    }
}

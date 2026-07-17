package com.johnmartin.coaching.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.johnmartin.coaching.properties.ExternalServicesProperties;

@Configuration
@EnableConfigurationProperties(ExternalServicesProperties.class)
public class AuthServiceClientConfig {

    @Bean
    public RestClient authServiceRestClient(ExternalServicesProperties properties) {
        return RestClient.builder().baseUrl(properties.getAuth().getUrl()).build();
    }
}

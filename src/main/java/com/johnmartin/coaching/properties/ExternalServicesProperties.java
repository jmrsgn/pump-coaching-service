package com.johnmartin.coaching.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "external-services")
public class ExternalServicesProperties {

    private final Service auth = new Service();
    private final Service social = new Service();

    @Setter
    @Getter
    public static class Service {
        private String url;
    }
}

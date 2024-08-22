package com.mvo.storagerest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt.password.encoder")
public class JwtProperties {
    private String secret;
    private Integer iteration;
    private Integer keyLength;
}

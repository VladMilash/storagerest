package com.mvo.storagerest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "yandex.storage")
public class YandexStorageProperties {
    private String accessKey;
    private String secretKey;

}
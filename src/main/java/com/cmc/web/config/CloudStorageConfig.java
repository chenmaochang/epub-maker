package com.cmc.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "cloud-storage.wenshushu")
@Configuration
@Data
public class CloudStorageConfig {
    private String account;
    private String password;
}

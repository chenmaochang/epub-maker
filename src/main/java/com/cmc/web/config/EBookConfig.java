package com.cmc.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "ebook.config")
@Configuration
@Data
public class EBookConfig {
    private String path;
}

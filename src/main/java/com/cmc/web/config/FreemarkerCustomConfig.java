package com.cmc.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "freemarker.config")
@Configuration
@Data
public class FreemarkerCustomConfig {
}

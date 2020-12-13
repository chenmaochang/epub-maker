package com.cmc.web.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "ebook.config")
@Configuration
@Data
public class EBookConfig {
    private String path;
    private String templateBookIndex;
    private String templateContainer;
    private String templateContentPage;
    private String templateCoverPage;
    private String templateMimetype;
    private String templateNcx;
    private String templateOpf;
}

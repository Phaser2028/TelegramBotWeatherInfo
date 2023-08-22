package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramBotProperties {
    private Bot bot;

    @Data
    public static class Bot {
        private String token;
        private String name;
    }
}
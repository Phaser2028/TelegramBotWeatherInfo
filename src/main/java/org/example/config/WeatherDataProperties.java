package org.example.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "yandex")
public class WeatherDataProperties {
    private WeatherProperties weather;
    private GeocodeProperties geocode;

    @Data
    public static class WeatherProperties {
        private String key;
        private String head;
        private String url;
    }

    @Data
    public static class GeocodeProperties {
        private String url;
        private String key;
    }
}





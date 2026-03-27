package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${stats.service.connect-timeout:10s}")
    private Duration connectTimeout;

    @Value("${stats.service.read-timeout:20s}")
    private Duration readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }
}
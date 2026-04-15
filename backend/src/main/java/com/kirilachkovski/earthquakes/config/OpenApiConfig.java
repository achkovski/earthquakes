package com.kirilachkovski.earthquakes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI seismoScoreOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SeismoScore API")
                .version("v1")
                .description("""
                        REST API for the SeismoScore earthquake monitoring system.
                        Exposes earthquake data sourced from the USGS GeoJSON feed and persisted locally.
                        """)
                .contact(new Contact()
                        .name("Kiril Achkovski")
                        .email("kirilachkovski@gmail.com"))
                .license(new License().name("Internal use")));
    }
}

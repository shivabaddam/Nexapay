package com.nexapay.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nexapay Banking API",
                version = "1.0.0",
                description = "Standalone in-memory banking API example",
                contact = @Contact(name = "Nexapay"),
                license = @License(name = "Proprietary")
        )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI();
    }
}
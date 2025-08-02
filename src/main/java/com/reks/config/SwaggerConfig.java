package com.reks.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI reksOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("REKS API")
                .description("Role-based Encrypted Keyword Search System")
                .version("v1.0")
                .license(new License().name("Apache 2.0")))
            .schemaRequirement("bearerAuth", 
                new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"));
    }
}
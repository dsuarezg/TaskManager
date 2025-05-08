package com.ironhack.TaskManager.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * This class customizes the OpenAPI documentation for the Task Manager API
 * and integrates JWT-based authentication into the API documentation.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Bean definition for the OpenAPI configuration.
     * This method customizes the API documentation with metadata and security settings.
     *
     * @return an OpenAPI object with the configured metadata and security scheme.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Name of the security scheme used for JWT authentication.

        return new OpenAPI()
                // Adds metadata about the API, such as title, version, and description.
                .info(new Info().title("Task Manager API").version("1.0").description("Task Manager API Documentation"))
                // Adds a security requirement to the API, specifying that the "bearerAuth" scheme is required.
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Configures the security scheme for JWT authentication.
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName) // Name of the security scheme.
                                .type(SecurityScheme.Type.HTTP) // Specifies that the scheme is HTTP-based.
                                .scheme("bearer") // Indicates that the scheme uses Bearer tokens.
                                .bearerFormat("JWT"))); // Specifies that the tokens are in JWT format.
    }
}
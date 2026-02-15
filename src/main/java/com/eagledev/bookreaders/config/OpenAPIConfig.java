package com.eagledev.bookreaders.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BookReaders API",
                version = "1.0.0",
                description = "Social Book Platform API\n" +
                        "This API powers the NextChapter application, handling Users, Books, " +
                        "Social interactions,.\n\n" +
                        "**Key Features:**\n" +
                        "- **Public:** Browse catalog, read reviews.\n" +
                        "- **User:** Manage profile, post, comment, like, follow authors.\n" +
                        "- **Admin:** CMS for Books/Authors.\n\n" +
                        "**Authentication:**\n" +
                        "This API uses JWT (JSON Web Tokens). Please log in via `/api/v1/auth/login` " +
                        "to obtain a token.",
                contact = @Contact(
                        name = "API Support",
                        email = "bookreaders@email.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development server (uses test data)")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
}

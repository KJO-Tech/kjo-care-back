package kjo.care.msvc_emergency.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.apache.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Microservicio Emergency API",
                description = "API para hacer un seguimiento de los recursos de emergencia",
                termsOfService = "www.kjo.dev",
                version = "1.0.0",
                contact = @Contact(
                        name = "Equipo KJO",
                        url = "https://www.kjo.dev",
                        email = "kjo@dev.com"
                ),
                license = @License(
                        name = "Standard Apache License Version 2.0 for Fintech",
                        url = "https://www.apache.org/licenses/LICENSE-2.0",
                        identifier = "Apache-2.0"
                )
        ),
        servers = {
                @Server(
                        description = "Local Server",
                        url = "http://localhost:9003"
                ),
                @Server(
                        description = "Production Server",
                        url = "https://"
                )
        }
)
@SecurityScheme(
        name = "securityToken",
        description = "Access Token For My API",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "Bearer",
        bearerFormat = "JWT"
)

public class SwaggerConfig {

}

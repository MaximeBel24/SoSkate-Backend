package com.soskate.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger pour l'API SoSkate
 */
@Configuration
public class OpenApiConfig {

    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SoSkate API")
                        .description("API REST pour la gestion des services de skateboard SoSkate")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Maxime Belin")
                                .email("maxime.b2494@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/soskate")
                                .description("Serveur de développement local")
                ));
    }
}

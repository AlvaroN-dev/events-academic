package com.codeup.riwi.tiqueteracatalogo.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Catálogo de Eventos y Venues")
                        .version("2.0.0")
                        .description("API REST para gestionar catálogo de eventos y lugares de la tiquetera online. " +
                                "Permite realizar operaciones CRUD sobre eventos y venues con autenticación JWT. " +
                                "Los endpoints de lectura (GET) son públicos, los de escritura requieren autenticación.")
                        .contact(new Contact()
                                .name("Equipo Tiquetera")
                                .email("soporte@tiquetera.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor de Pruebas")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT de autenticación. Obténelo en /auth/login")));
    }
}

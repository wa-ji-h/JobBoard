package com.educonnect.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TP - Partie 2, methode 1 : configuration STATIQUE via une classe Java.
 *
 * Les URI pointent directement vers les ports des microservices, sans passer
 * par Eureka. Actif uniquement avec le profil "static".
 */
@Configuration
@Profile("static")
public class StaticRouteConfig {

    @Bean
    public RouteLocator staticRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("candidat-static", route -> route
                        .path("/candidats/**")
                        .uri("http://localhost:8081"))
                .route("job-static", route -> route
                        .path("/jobs/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}

package com.educonnect.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TP - Partie 3, methode 1 : configuration DYNAMIQUE via une classe Java.
 *
 * Les URI utilisent le schema "lb://" : la Gateway interroge Eureka pour
 * trouver une instance disponible et applique le load balancing.
 * Actif uniquement avec le profil "dynamic-class".
 */
@Configuration
@Profile("dynamic-class")
public class DynamicRouteConfig {

    @Bean
    public RouteLocator dynamicRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("candidat-dynamic", route -> route
                        .path("/candidats/**")
                        .uri("lb://candidat-service"))
                .route("job-dynamic", route -> route
                        .path("/jobs/**")
                        .uri("lb://job-service"))
                .build();
    }
}

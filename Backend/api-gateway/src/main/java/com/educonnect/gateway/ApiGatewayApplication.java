package com.educonnect.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entree unique de la plateforme EduConnect.
 *
 * La Gateway route les requetes vers les microservices :
 * - /candidats/**  -> candidat-service
 * - /jobs/**       -> job-service
 *
 * Le routage peut etre statique (URL directe) ou dynamique (Eureka + lb://).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

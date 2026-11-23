package com.educonnect.gateway.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TP - Partie 3, question 3 : applique l'algorithme RANDOM uniquement au
 * service "candidat-service". "job-service" reste en RoundRobin (defaut).
 *
 * Pour revenir au RoundRobin sur candidat (comparaison), ajouter le profil
 * "round-robin" : --spring.profiles.active=dynamic,round-robin
 */
@Configuration
@Profile("!round-robin")
@LoadBalancerClient(name = "candidat-service", configuration = RandomLoadBalancerConfig.class)
public class LoadBalancerConfig {
}

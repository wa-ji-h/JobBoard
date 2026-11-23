package com.educonnect.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint de demonstration utilise pour verifier le routage via l'API Gateway
 * et le load balancing entre plusieurs instances.
 */
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Value("${server.port:8082}")
    private String port;

    @GetMapping
    public Map<String, Object> lister() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "JOB-SERVICE");
        response.put("message", "Liste des offres d'emploi");
        response.put("port", port);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "JOB-SERVICE");
        response.put("id", id);
        response.put("port", port);
        return response;
    }
}

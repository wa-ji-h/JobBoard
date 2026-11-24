package com.educonnect.gateway;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoadBalancingCheck {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost:8090/candidats"))
                .GET()
                .build();

        for (int i = 1; i <= 20; i++) {
            String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            String port = body.replaceAll("(?s).*\"port\"\\s*:\\s*(\\d+).*", "$1");
            System.out.println("Requête " + i + " -> instance sur le port " + port);
        }
    }
}
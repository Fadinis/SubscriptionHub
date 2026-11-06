package com.subscriptionhub.notification.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public SubscriptionClient() {
        this("http://localhost:8082");
    }

    public SubscriptionClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        this.mapper = new ObjectMapper();
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    public List<SubscriptionDto> buscarPorUsuarioId(int usuarioId) {
        String url = baseUrl + "/api/subscriptions/user/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<SubscriptionDto>>() {});
            } else if (response.statusCode() == 404) {
                return new ArrayList<>();
            } else {
                System.err.println("[SubscriptionClient] Resposta inesperada: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Requisicao interrompida: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel conectar ao Subscription Service em " + baseUrl + " (porta 8082).", e);
        }
    }
}


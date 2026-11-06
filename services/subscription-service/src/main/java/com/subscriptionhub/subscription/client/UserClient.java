package com.subscriptionhub.subscription.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class UserClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public UserClient() {
        this("http://localhost:8081");
    }

    public UserClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        this.mapper = new ObjectMapper();
    }

    public Optional<UserDto> buscarUsuarioPorId(int id) {
        String url = baseUrl + "/api/users/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                UserDto dto = mapper.readValue(response.body(), UserDto.class);
                return Optional.of(dto);
            } else if (response.statusCode() == 404) {
                return Optional.empty();
            } else {
                System.err.println("[UserClient] Resposta inesperada do User Service: " + response.statusCode());
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Requisicao ao User Service interrompida: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel conectar ao User Service em " + baseUrl + ". Verifique se o servico esta ativo na porta 8081.", e);
        }
    }
}


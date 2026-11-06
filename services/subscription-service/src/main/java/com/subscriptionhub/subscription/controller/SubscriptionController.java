package com.subscriptionhub.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.subscriptionhub.subscription.model.Subscription;
import com.subscriptionhub.subscription.service.SubscriptionService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

public class SubscriptionController {
    private final SubscriptionService service;
    private final ObjectMapper mapper;
    private HttpServer server;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/subscriptions/health", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, Map.of("status", "UP", "service", "subscription-service", "port", port));
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.createContext("/api/subscriptions", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("POST".equalsIgnoreCase(method)) {
                handleCriarAssinatura(exchange);
            } else if ("GET".equalsIgnoreCase(method)) {
                if (path.startsWith("/api/subscriptions/user/")) {
                    handleBuscarPorUsuario(exchange, path);
                } else if (path.matches("^/api/subscriptions/\\d+$")) {
                    handleBuscarPorId(exchange, path);
                } else if (path.equals("/api/subscriptions") || path.equals("/api/subscriptions/")) {
                    handleListarTodas(exchange);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Endpoint nao encontrado"));
                }
            } else if ("PUT".equalsIgnoreCase(method) && path.endsWith("/cancel")) {
                handleCancelar(exchange, path);
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.start();
        System.out.println("==================================================");
        System.out.println(" [SUBSCRIPTION-SERVICE] Rodando com sucesso na porta " + port);
        System.out.println(" - Health:   http://localhost:" + port + "/api/subscriptions/health");
        System.out.println(" - Listar:   http://localhost:" + port + "/api/subscriptions");
        System.out.println(" - Por User: http://localhost:" + port + "/api/subscriptions/user/{usuarioId}");
        System.out.println(" - Criar:    POST http://localhost:" + port + "/api/subscriptions");
        System.out.println(" - Cancelar: PUT  http://localhost:" + port + "/api/subscriptions/{id}/cancel");
        System.out.println("==================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handleCriarAssinatura(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            Subscription sub = mapper.readValue(is, Subscription.class);
            Subscription salva = service.cadastrar(sub);
            sendJson(exchange, 201, salva);
        } catch (IllegalArgumentException e) {
            int code = e.getMessage().contains("nao encontrado no User Service") ? 422 : 400;
            sendJson(exchange, code, Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            // Falha de comunicação de rede inter-serviços
            sendJson(exchange, 503, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 400, Map.of("error", "Requisicao invalida: " + e.getMessage()));
        }
    }

    private void handleBuscarPorUsuario(HttpExchange exchange, String path) throws IOException {
        try {
            int usuarioId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            List<Subscription> subs = service.buscarPorUsuarioId(usuarioId);
            sendJson(exchange, 200, subs);
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, Map.of("error", "ID de usuario invalido"));
        }
    }

    private void handleBuscarPorId(HttpExchange exchange, String path) throws IOException {
        try {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            Optional<Subscription> sub = service.buscarPorId(id);
            if (sub.isPresent()) {
                sendJson(exchange, 200, sub.get());
            } else {
                sendJson(exchange, 404, Map.of("error", "Assinatura nao encontrada para o ID: " + id));
            }
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, Map.of("error", "ID invalido"));
        }
    }

    private void handleCancelar(HttpExchange exchange, String path) throws IOException {
        try {
            // Path: /api/subscriptions/{id}/cancel
            String[] parts = path.split("/");
            if (parts.length >= 4) {
                int id = Integer.parseInt(parts[3]);
                Subscription cancelada = service.cancelar(id);
                sendJson(exchange, 200, Map.of("mensagem", "Assinatura cancelada com sucesso", "assinatura", cancelada));
            } else {
                sendJson(exchange, 400, Map.of("error", "Formato de URL invalido para cancelamento"));
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 404, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 400, Map.of("error", e.getMessage()));
        }
    }

    private void handleListarTodas(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, service.listarTodas());
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}


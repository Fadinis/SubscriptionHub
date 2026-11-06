package com.subscriptionhub.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.subscriptionhub.notification.model.CheckNotificationRequest;
import com.subscriptionhub.notification.model.Notification;
import com.subscriptionhub.notification.service.NotificationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

public class NotificationController {
    private final NotificationService service;
    private final ObjectMapper mapper;
    private HttpServer server;

    public NotificationController(NotificationService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/notifications/health", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, Map.of("status", "UP", "service", "notification-service", "port", port));
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.createContext("/api/notifications/check", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleVerificarVencimentos(exchange);
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.createContext("/api/notifications", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equalsIgnoreCase(method)) {
                if (path.startsWith("/api/notifications/user/")) {
                    handleBuscarPorUsuario(exchange, path);
                } else if (path.equals("/api/notifications") || path.equals("/api/notifications/")) {
                    handleListarTodas(exchange);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Endpoint nao encontrado"));
                }
            } else if ("PUT".equalsIgnoreCase(method) && path.endsWith("/read")) {
                handleMarcarComoLida(exchange, path);
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.start();
        System.out.println("==================================================");
        System.out.println(" [NOTIFICATION-SERVICE] Rodando com sucesso na porta " + port);
        System.out.println(" - Health:   http://localhost:" + port + "/api/notifications/health");
        System.out.println(" - Listar:   http://localhost:" + port + "/api/notifications");
        System.out.println(" - Por User: http://localhost:" + port + "/api/notifications/user/{usuarioId}");
        System.out.println(" - Checar:   POST http://localhost:" + port + "/api/notifications/check");
        System.out.println(" - Ler:      PUT  http://localhost:" + port + "/api/notifications/{id}/read");
        System.out.println("==================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handleVerificarVencimentos(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            CheckNotificationRequest req = mapper.readValue(is, CheckNotificationRequest.class);
            List<Notification> geradas = service.verificarVencimentosUsuario(req.getUsuarioId(), req.getDiasAntecedencia());
            sendJson(exchange, 200, Map.of("mensagem", "Verificacao concluida com sucesso", "totalGerados", geradas.size(), "alertas", geradas));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            // Falha de comunicação inter-serviços
            sendJson(exchange, 503, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 400, Map.of("error", "Requisicao invalida: " + e.getMessage()));
        }
    }

    private void handleBuscarPorUsuario(HttpExchange exchange, String path) throws IOException {
        try {
            int usuarioId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            List<Notification> list = service.listarPorUsuario(usuarioId);
            sendJson(exchange, 200, list);
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, Map.of("error", "ID de usuario invalido"));
        }
    }

    private void handleMarcarComoLida(HttpExchange exchange, String path) throws IOException {
        try {
            // Path: /api/notifications/{id}/read
            String[] parts = path.split("/");
            if (parts.length >= 4) {
                int id = Integer.parseInt(parts[3]);
                Optional<Notification> opt = service.marcarComoLida(id);
                if (opt.isPresent()) {
                    sendJson(exchange, 200, Map.of("mensagem", "Notificacao marcada como lida", "notificacao", opt.get()));
                } else {
                    sendJson(exchange, 404, Map.of("error", "Notificacao nao encontrada para o ID: " + id));
                }
            } else {
                sendJson(exchange, 400, Map.of("error", "Formato de URL invalido"));
            }
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


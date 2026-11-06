package com.subscriptionhub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.subscriptionhub.user.model.LoginRequest;
import com.subscriptionhub.user.model.User;
import com.subscriptionhub.user.model.UserResponse;
import com.subscriptionhub.user.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

public class UserController {
    private final UserService service;
    private final ObjectMapper mapper;
    private HttpServer server;

    public UserController(UserService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/users/health", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, Map.of("status", "UP", "service", "user-service", "port", port));
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.createContext("/api/users/login", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleLogin(exchange);
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.createContext("/api/users", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("POST".equalsIgnoreCase(method)) {
                handleCadastro(exchange);
            } else if ("GET".equalsIgnoreCase(method)) {
                if (path.matches("^/api/users/\\d+$")) {
                    handleBuscarPorId(exchange, path);
                } else if (path.equals("/api/users") || path.equals("/api/users/")) {
                    handleListarTodos(exchange);
                } else {
                    sendJson(exchange, 404, Map.of("error", "Endpoint nao encontrado"));
                }
            } else {
                sendJson(exchange, 405, Map.of("error", "Metodo nao permitido"));
            }
        });

        server.start();
        System.out.println("==================================================");
        System.out.println(" [USER-SERVICE] Rodando com sucesso na porta " + port);
        System.out.println(" - Health:   http://localhost:" + port + "/api/users/health");
        System.out.println(" - Listar:   http://localhost:" + port + "/api/users");
        System.out.println(" - Buscar:   http://localhost:" + port + "/api/users/{id}");
        System.out.println(" - Cadastro: POST http://localhost:" + port + "/api/users");
        System.out.println(" - Login:    POST http://localhost:" + port + "/api/users/login");
        System.out.println("==================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handleCadastro(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            User user = mapper.readValue(is, User.class);
            UserResponse criado = service.cadastrar(user);
            sendJson(exchange, 201, criado);
        } catch (IllegalArgumentException e) {
            int status = e.getMessage().contains("ja cadastrado") ? 409 : 400;
            sendJson(exchange, status, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 400, Map.of("error", "Requisicao invalida: " + e.getMessage()));
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            LoginRequest req = mapper.readValue(is, LoginRequest.class);
            Optional<UserResponse> auth = service.autenticar(req.getEmail(), req.getSenha());
            if (auth.isPresent()) {
                sendJson(exchange, 200, Map.of("mensagem", "Autenticado com sucesso", "usuario", auth.get()));
            } else {
                sendJson(exchange, 401, Map.of("error", "Credenciais invalidas"));
            }
        } catch (Exception e) {
            sendJson(exchange, 400, Map.of("error", "Corpo da requisicao invalido: " + e.getMessage()));
        }
    }

    private void handleBuscarPorId(HttpExchange exchange, String path) throws IOException {
        try {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            Optional<UserResponse> user = service.buscarPorId(id);
            if (user.isPresent()) {
                sendJson(exchange, 200, user.get());
            } else {
                sendJson(exchange, 404, Map.of("error", "Usuario nao encontrado para o ID: " + id));
            }
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, Map.of("error", "ID de usuario invalido"));
        }
    }

    private void handleListarTodos(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, service.listarTodos());
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


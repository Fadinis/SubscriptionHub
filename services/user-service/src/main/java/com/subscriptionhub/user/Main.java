package com.subscriptionhub.user;

import com.subscriptionhub.user.controller.UserController;
import com.subscriptionhub.user.repository.UserRepository;
import com.subscriptionhub.user.service.UserService;

public class Main {
    public static final int PORT = 8081;

    public static void main(String[] args) {
        try {
            UserRepository repository = new UserRepository();
            UserService service = new UserService(repository);
            UserController controller = new UserController(service);
            controller.start(PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[USER-SERVICE] Encerrando servico...");
                controller.stop();
            }));
        } catch (Exception e) {
            System.err.println("[USER-SERVICE] Erro fatal ao iniciar servico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


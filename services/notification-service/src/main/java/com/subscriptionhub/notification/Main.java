package com.subscriptionhub.notification;

import com.subscriptionhub.notification.client.SubscriptionClient;
import com.subscriptionhub.notification.controller.NotificationController;
import com.subscriptionhub.notification.repository.NotificationRepository;
import com.subscriptionhub.notification.service.NotificationService;

public class Main {
    public static final int PORT = 8083;

    public static void main(String[] args) {
        try {
            NotificationRepository repository = new NotificationRepository();
            SubscriptionClient subscriptionClient = new SubscriptionClient("http://localhost:8082");
            NotificationService service = new NotificationService(repository, subscriptionClient);
            NotificationController controller = new NotificationController(service);
            controller.start(PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[NOTIFICATION-SERVICE] Encerrando servico...");
                controller.stop();
            }));
        } catch (Exception e) {
            System.err.println("[NOTIFICATION-SERVICE] Erro fatal ao iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


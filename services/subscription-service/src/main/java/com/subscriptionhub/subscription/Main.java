package com.subscriptionhub.subscription;

import com.subscriptionhub.subscription.client.UserClient;
import com.subscriptionhub.subscription.controller.SubscriptionController;
import com.subscriptionhub.subscription.repository.SubscriptionRepository;
import com.subscriptionhub.subscription.service.SubscriptionService;

public class Main {
    public static final int PORT = 8082;

    public static void main(String[] args) {
        try {
            SubscriptionRepository repository = new SubscriptionRepository();
            UserClient userClient = new UserClient("http://localhost:8081");
            SubscriptionService service = new SubscriptionService(repository, userClient);
            SubscriptionController controller = new SubscriptionController(service);
            controller.start(PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[SUBSCRIPTION-SERVICE] Encerrando servico...");
                controller.stop();
            }));
        } catch (Exception e) {
            System.err.println("[SUBSCRIPTION-SERVICE] Erro fatal ao iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


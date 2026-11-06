package com.subscriptionhub.notification.service;

import com.subscriptionhub.notification.client.SubscriptionClient;
import com.subscriptionhub.notification.client.SubscriptionDto;
import com.subscriptionhub.notification.model.Notification;
import com.subscriptionhub.notification.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class NotificationService {
    private final NotificationRepository repository;
    private final SubscriptionClient subscriptionClient;

    public NotificationService(NotificationRepository repository, SubscriptionClient subscriptionClient) {
        this.repository = repository;
        this.subscriptionClient = subscriptionClient;
    }

    public List<Notification> verificarVencimentosUsuario(int usuarioId, int diasAntecedencia) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario invalido.");
        }
        if (diasAntecedencia < 0) {
            throw new IllegalArgumentException("Dias de antecedencia nao pode ser negativo: " + diasAntecedencia);
        }

        List<SubscriptionDto> assinaturas;
        try {
            // Comunicação REST Inter-serviços com Subscription Service (MS-2)
            assinaturas = subscriptionClient.buscarPorUsuarioId(usuarioId);
        } catch (IllegalStateException e) {
            System.err.println("[NotificationService] Falha de conexao com Subscription Service: " + e.getMessage());
            throw e;
        }

        List<Notification> novasNotificacoes = new ArrayList<>();
        long agoraMillis = System.currentTimeMillis();

        for (SubscriptionDto sub : assinaturas) {
            if ("ATIVA".equalsIgnoreCase(sub.getStatus()) && sub.getDataVencimento() != null) {
                long diffMillis = sub.getDataVencimento().getTime() - agoraMillis;
                long diasRestantes = TimeUnit.MILLISECONDS.toDays(diffMillis);

                // Se vence dentro da janela (ex: hoje até diasAntecedencia dias)
                if (diffMillis >= -TimeUnit.DAYS.toMillis(1) && diasRestantes <= diasAntecedencia) {
                    String msg = String.format("Alerta de Vencimento: A assinatura '%s' (R$ %.2f) vence em %d dia(s)!",
                            sub.getNomeServico(), sub.getValor(), Math.max(0, diasRestantes));

                    Notification notif = new Notification(0, usuarioId, sub.getId(), msg, new Date(), false);
                    Notification salva = repository.save(notif);
                    novasNotificacoes.add(salva);
                }
            }
        }

        return novasNotificacoes;
    }

    public Optional<Notification> marcarComoLida(int id) {
        Optional<Notification> opt = repository.findById(id);
        if (opt.isPresent()) {
            Notification n = opt.get();
            n.setLida(true);
            repository.save(n);
            return Optional.of(n);
        }
        return Optional.empty();
    }

    public List<Notification> listarPorUsuario(int usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Notification> listarTodas() {
        return repository.findAll();
    }
}


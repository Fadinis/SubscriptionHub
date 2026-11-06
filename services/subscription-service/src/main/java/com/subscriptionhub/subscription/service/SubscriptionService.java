package com.subscriptionhub.subscription.service;

import com.subscriptionhub.subscription.client.UserClient;
import com.subscriptionhub.subscription.client.UserDto;
import com.subscriptionhub.subscription.model.Subscription;
import com.subscriptionhub.subscription.repository.SubscriptionRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserClient userClient;

    public SubscriptionService(SubscriptionRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    public Subscription cadastrar(Subscription sub) {
        if (sub == null) {
            throw new IllegalArgumentException("Dados de assinatura nao podem ser nulos.");
        }
        if (sub.getNomeServico() == null || sub.getNomeServico().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do servico e obrigatorio.");
        }
        if (sub.getValor() <= 0.0) {
            throw new IllegalArgumentException("Valor da assinatura deve ser maior que zero.");
        }
        if (sub.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("ID do usuario associado e obrigatorio.");
        }

        // Validação REST Inter-serviços com User Service (MS-1)
        Optional<UserDto> userDto = userClient.buscarUsuarioPorId(sub.getUsuarioId());
        if (userDto.isEmpty()) {
            throw new IllegalArgumentException("Usuario nao encontrado no User Service para o ID: " + sub.getUsuarioId());
        }

        if (sub.getDataVencimento() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 30);
            sub.setDataVencimento(cal.getTime());
        }

        if (sub.getStatus() == null || sub.getStatus().trim().isEmpty()) {
            sub.setStatus("ATIVA");
        }

        return repository.save(sub);
    }

    public Subscription cancelar(int id) {
        Optional<Subscription> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Assinatura nao encontrada para o ID: " + id);
        }
        Subscription sub = opt.get();
        sub.setStatus("CANCELADA");
        return repository.save(sub);
    }

    public Optional<Subscription> buscarPorId(int id) {
        return repository.findById(id);
    }

    public List<Subscription> buscarPorUsuarioId(int usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Subscription> listarTodas() {
        return repository.findAll();
    }
}


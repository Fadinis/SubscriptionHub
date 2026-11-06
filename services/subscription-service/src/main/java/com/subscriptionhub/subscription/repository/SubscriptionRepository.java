package com.subscriptionhub.subscription.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.subscriptionhub.subscription.model.Periodicidade;
import com.subscriptionhub.subscription.model.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubscriptionRepository {
    private final File dataFile;
    private final ObjectMapper mapper;
    private final List<Subscription> cache;

    public SubscriptionRepository() {
        this(new File("services/subscription-service/data/subscriptions.json"));
    }

    public SubscriptionRepository(File dataFile) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.cache = new ArrayList<>();
        carregar();
    }

    private synchronized void carregar() {
        cache.clear();
        if (dataFile.exists()) {
            try {
                List<Subscription> list = mapper.readValue(dataFile, new TypeReference<List<Subscription>>() {});
                if (list != null) {
                    cache.addAll(list);
                }
            } catch (IOException e) {
                System.err.println("[SubscriptionRepository] Erro ao carregar: " + e.getMessage());
            }
        }
        if (cache.isEmpty()) {
            // Inicializa com dados para testes
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 2); // Vencimento em 2 dias (ideal para testar alertas!)
            Date vencimentoProximo = cal.getTime();

            cal.add(Calendar.DAY_OF_MONTH, 20); // Vencimento em 22 dias
            Date vencimentoDistante = cal.getTime();

            cache.add(new Subscription(1, 1, "Netflix Premium", 55.90, vencimentoProximo, "ATIVA", "Streaming", Periodicidade.MENSAL));
            cache.add(new Subscription(2, 1, "Spotify Familia", 34.90, vencimentoDistante, "ATIVA", "Musica", Periodicidade.MENSAL));
            salvar();
        }
    }

    private synchronized void salvar() {
        try {
            if (dataFile.getParentFile() != null && !dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            mapper.writeValue(dataFile, cache);
        } catch (IOException e) {
            System.err.println("[SubscriptionRepository] Erro ao salvar: " + e.getMessage());
        }
    }

    public synchronized List<Subscription> findAll() {
        return new ArrayList<>(cache);
    }

    public synchronized Optional<Subscription> findById(int id) {
        return cache.stream().filter(s -> s.getId() == id).findFirst();
    }

    public synchronized List<Subscription> findByUsuarioId(int usuarioId) {
        return cache.stream().filter(s -> s.getUsuarioId() == usuarioId).collect(Collectors.toList());
    }

    public synchronized Subscription save(Subscription sub) {
        if (sub.getId() <= 0) {
            int maxId = cache.stream().mapToInt(Subscription::getId).max().orElse(0);
            sub.setId(maxId + 1);
        } else {
            cache.removeIf(s -> s.getId() == sub.getId());
        }
        cache.add(sub);
        salvar();
        return sub;
    }

    public synchronized boolean deleteById(int id) {
        boolean removed = cache.removeIf(s -> s.getId() == id);
        if (removed) {
            salvar();
        }
        return removed;
    }
}


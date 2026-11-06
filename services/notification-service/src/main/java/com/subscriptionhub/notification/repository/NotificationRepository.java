package com.subscriptionhub.notification.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.subscriptionhub.notification.model.Notification;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NotificationRepository {
    private final File dataFile;
    private final ObjectMapper mapper;
    private final List<Notification> cache;

    public NotificationRepository() {
        this(new File("services/notification-service/data/notifications.json"));
    }

    public NotificationRepository(File dataFile) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        this.cache = new ArrayList<>();
        carregar();
    }

    private synchronized void carregar() {
        cache.clear();
        if (dataFile.exists()) {
            try {
                List<Notification> list = mapper.readValue(dataFile, new TypeReference<List<Notification>>() {});
                if (list != null) {
                    cache.addAll(list);
                }
            } catch (IOException e) {
                System.err.println("[NotificationRepository] Erro ao carregar: " + e.getMessage());
            }
        }
    }

    private synchronized void salvar() {
        try {
            if (dataFile.getParentFile() != null && !dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            mapper.writeValue(dataFile, cache);
        } catch (IOException e) {
            System.err.println("[NotificationRepository] Erro ao salvar: " + e.getMessage());
        }
    }

    public synchronized List<Notification> findAll() {
        return new ArrayList<>(cache);
    }

    public synchronized Optional<Notification> findById(int id) {
        return cache.stream().filter(n -> n.getId() == id).findFirst();
    }

    public synchronized List<Notification> findByUsuarioId(int usuarioId) {
        return cache.stream().filter(n -> n.getUsuarioId() == usuarioId).collect(Collectors.toList());
    }

    public synchronized Notification save(Notification notif) {
        if (notif.getId() <= 0) {
            int maxId = cache.stream().mapToInt(Notification::getId).max().orElse(0);
            notif.setId(maxId + 1);
        } else {
            cache.removeIf(n -> n.getId() == notif.getId());
        }
        cache.add(notif);
        salvar();
        return notif;
    }
}


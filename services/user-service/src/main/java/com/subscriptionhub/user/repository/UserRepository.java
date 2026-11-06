package com.subscriptionhub.user.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.subscriptionhub.user.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final File dataFile;
    private final ObjectMapper mapper;
    private final List<User> cache;

    public UserRepository() {
        this(new File("services/user-service/data/users.json"));
    }

    public UserRepository(File dataFile) {
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
                List<User> list = mapper.readValue(dataFile, new TypeReference<List<User>>() {});
                if (list != null) {
                    cache.addAll(list);
                }
            } catch (IOException e) {
                System.err.println("[UserRepository] Falha ao carregar dados: " + e.getMessage());
            }
        }
        if (cache.isEmpty()) {
            // Inicializar com usuários padrão para testes
            cache.add(new User(1, "Joao Silva", "joao@email.com", "senha123"));
            cache.add(new User(2, "Maria Santos", "maria@email.com", "m12345"));
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
            System.err.println("[UserRepository] Falha ao salvar dados: " + e.getMessage());
        }
    }

    public synchronized List<User> findAll() {
        return new ArrayList<>(cache);
    }

    public synchronized Optional<User> findById(int id) {
        return cache.stream().filter(u -> u.getId() == id).findFirst();
    }

    public synchronized Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return cache.stream().filter(u -> u.getEmail().equalsIgnoreCase(email.trim())).findFirst();
    }

    public synchronized User save(User user) {
        if (user.getId() <= 0) {
            int maxId = cache.stream().mapToInt(User::getId).max().orElse(0);
            user.setId(maxId + 1);
        } else {
            cache.removeIf(u -> u.getId() == user.getId());
        }
        cache.add(user);
        salvar();
        return user;
    }
}


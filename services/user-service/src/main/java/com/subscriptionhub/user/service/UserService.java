package com.subscriptionhub.user.service;

import com.subscriptionhub.user.model.User;
import com.subscriptionhub.user.model.UserResponse;
import com.subscriptionhub.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse cadastrar(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Dados de usuario nao podem ser nulos.");
        }
        if (user.getNome() == null || user.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email invalido.");
        }
        if (user.getSenha() == null || user.getSenha().trim().length() < 4) {
            throw new IllegalArgumentException("Senha deve possuir pelo menos 4 caracteres.");
        }

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setNome(user.getNome().trim());

        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ja cadastrado no sistema: " + user.getEmail());
        }

        User salvo = repository.save(user);
        return UserResponse.fromUser(salvo);
    }

    public Optional<UserResponse> autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return Optional.empty();
        }
        return repository.findByEmail(email.trim().toLowerCase())
                .filter(u -> u.getSenha() != null && u.getSenha().equals(senha))
                .map(UserResponse::fromUser);
    }

    public Optional<UserResponse> buscarPorId(int id) {
        return repository.findById(id).map(UserResponse::fromUser);
    }

    public List<UserResponse> listarTodos() {
        return repository.findAll().stream().map(UserResponse::fromUser).collect(Collectors.toList());
    }
}


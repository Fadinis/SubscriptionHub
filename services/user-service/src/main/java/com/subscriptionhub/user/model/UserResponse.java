package com.subscriptionhub.user.model;

public class UserResponse {
    private int id;
    private String nome;
    private String email;

    public UserResponse() {}

    public UserResponse(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public static UserResponse fromUser(User user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getNome(), user.getEmail());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


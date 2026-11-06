package com.subscriptionhub.notification.model;

import java.util.Date;

public class Notification {
    private int id;
    private int usuarioId;
    private int assinaturaId;
    private String mensagem;
    private Date dataCriacao;
    private boolean lida;

    public Notification() {
        this.dataCriacao = new Date();
        this.lida = false;
    }

    public Notification(int id, int usuarioId, int assinaturaId, String mensagem, Date dataCriacao, boolean lida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.assinaturaId = assinaturaId;
        this.mensagem = mensagem;
        this.dataCriacao = (dataCriacao != null) ? dataCriacao : new Date();
        this.lida = lida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getAssinaturaId() {
        return assinaturaId;
    }

    public void setAssinaturaId(int assinaturaId) {
        this.assinaturaId = assinaturaId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }
}


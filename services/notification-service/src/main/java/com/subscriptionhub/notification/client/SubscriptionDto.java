package com.subscriptionhub.notification.client;

import java.util.Date;

public class SubscriptionDto {
    private int id;
    private int usuarioId;
    private String nomeServico;
    private double valor;
    private Date dataVencimento;
    private String status;

    public SubscriptionDto() {}

    public SubscriptionDto(int id, int usuarioId, String nomeServico, double valor, Date dataVencimento, String status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nomeServico = nomeServico;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
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

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


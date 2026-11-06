package com.subscriptionhub.subscription.model;

import java.util.Date;

public class Subscription {
    private int id;
    private int usuarioId;
    private String nomeServico;
    private double valor;
    private Date dataVencimento;
    private String status; // "ATIVA", "CANCELADA", "SUSPENSA"
    private String categoria;
    private Periodicidade periodicidade;

    public Subscription() {
        this.status = "ATIVA";
        this.periodicidade = Periodicidade.MENSAL;
    }

    public Subscription(int id, int usuarioId, String nomeServico, double valor, Date dataVencimento, String status, String categoria, Periodicidade periodicidade) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nomeServico = nomeServico;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = (status != null && !status.isEmpty()) ? status : "ATIVA";
        this.categoria = categoria;
        this.periodicidade = (periodicidade != null) ? periodicidade : Periodicidade.MENSAL;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }
}


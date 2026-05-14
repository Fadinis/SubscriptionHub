package model;

import java.util.Date;

public class Alerta {
    private int id;
    private String mensagem;
    private int diasAntecedencia;
    private Date dataDisparo;
    private boolean lido;

    public Alerta() {}

    public Alerta(int id, String mensagem, int diasAntecedencia, Date dataDisparo, boolean lido) {
        this.id = id;
        this.mensagem = mensagem;
        this.diasAntecedencia = diasAntecedencia;
        this.dataDisparo = dataDisparo;
        this.lido = lido;
    }

    public void dispararAlerta() {
        System.out.println("Alerta disparado: " + mensagem);
    }

    public void marcarComoLido() {
        this.lido = true;
        System.out.println("Alerta marcado como lido.");
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getDiasAntecedencia() {
        return diasAntecedencia;
    }

    public void setDiasAntecedencia(int diasAntecedencia) {
        this.diasAntecedencia = diasAntecedencia;
    }

    public Date getDataDisparo() {
        return dataDisparo;
    }

    public void setDataDisparo(Date dataDisparo) {
        this.dataDisparo = dataDisparo;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }
}

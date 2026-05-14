package model;

import java.util.Date;

public class LogAcao {
    private int id;
    private String acao;
    private Date dataHora;
    private String ip;

    public LogAcao() {}

    public LogAcao(int id, String acao, Date dataHora, String ip) {
        this.id = id;
        this.acao = acao;
        this.dataHora = dataHora;
        this.ip = ip;
    }

    public void registrarLog() {
        System.out.println("Log registrado: [" + dataHora + "] " + acao + " (IP: " + ip + ")");
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

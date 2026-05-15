package model;

import java.util.List;
import java.util.ArrayList;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senhaHash;
    private List<Assinatura> assinaturas;
    private List<RelatorioFinanceiro> relatorios;
    private List<Alerta> alertas;
    private List<LogAcao> logs;

    public Usuario() {
        this.assinaturas = new ArrayList<>();
        this.relatorios = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    public Usuario(int id, String nome, String email, String senhaPlana) {
        this();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = gerarHash(senhaPlana);
    }

    public static String gerarHash(String senha) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return senha; // Fallback (não ideal, mas evita crash)
        }
    }

    public boolean autenticar(String senhaPlana) {
        String hashTentativa = gerarHash(senhaPlana);
        System.out.println("Autenticando usuário " + email + " (Hash validado)");
        return this.senhaHash.equals(hashTentativa);
    }

    public void configurarAlerta() {
        System.out.println("Configurando alerta para o usuário " + nome);
    }

    // Getters e Setters
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public List<Assinatura> getAssinaturas() {
        return assinaturas;
    }

    public void setAssinaturas(List<Assinatura> assinaturas) {
        this.assinaturas = assinaturas;
    }

    public List<RelatorioFinanceiro> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(List<RelatorioFinanceiro> relatorios) {
        this.relatorios = relatorios;
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<Alerta> alertas) {
        this.alertas = alertas;
    }

    public List<LogAcao> getLogs() {
        return logs;
    }

    public void setLogs(List<LogAcao> logs) {
        this.logs = logs;
    }
}

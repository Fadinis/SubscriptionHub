package model;

import java.util.List;


public class Administrador extends Usuario {
    private int nivelAcesso;

    public Administrador() {
        super();
    }

    public Administrador(int id, String nome, String email, String senhaHash, int nivelAcesso) {
        super(id, nome, email, senhaHash);
        this.nivelAcesso = nivelAcesso;
    }

    public List<LogAcao> auditarLogs() {
        System.out.println("Administrador " + getNome() + " auditando logs...");
        return getLogs(); // Or a global list if needed, but per diagram it seems related to logs.
    }

    // Getters e Setters
    public int getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
}

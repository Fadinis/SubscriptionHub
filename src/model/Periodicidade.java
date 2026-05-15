package model;

/**
 * Define os tipos de recorrencia das assinaturas.
 */
public enum Periodicidade {
    MENSAL("Mensal", 1),
    SEMANAL("Semanal", 0), // Representa menos de um mes
    TRIMESTRAL("Trimestral", 3),
    SEMESTRAL("Semestral", 6),
    ANUAL("Anual", 12);

    private final String descricao;
    private final int meses;

    Periodicidade(String descricao, int meses) {
        this.descricao = descricao;
        this.meses = meses;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMeses() {
        return meses;
    }
}

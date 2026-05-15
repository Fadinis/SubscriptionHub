package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RelatorioFinanceiro {
    private int id;
    private int mesReferencia;
    private int anoReferencia;
    private double totalGasto;
    private double custoPlanejadoMensal; // Novo: soma dos equivalentes mensais
    private Map<String, Double> gastosPorCategoria;
    private boolean dadosEncontrados;

    public RelatorioFinanceiro() {
        this.gastosPorCategoria = new HashMap<>();
        this.dadosEncontrados = false;
    }

    public void solicitarRelatorio(int mes, int ano, List<Assinatura> assinaturas) {
        this.mesReferencia = mes;
        this.anoReferencia = ano;
        List<Assinatura> dadosPeriodo = buscarDadosAssinaturas(mes, ano, assinaturas);

        if (dadosPeriodo.isEmpty()) {
            this.dadosEncontrados = false;
        } else {
            this.dadosEncontrados = true;
            calcularTotais(dadosPeriodo);
        }
        gerarRelatorio();
    }

    public List<Assinatura> buscarDadosAssinaturas(int mes, int ano, List<Assinatura> todasAssinaturas) {
        List<Assinatura> resultado = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();

        for (Assinatura a : todasAssinaturas) {
            if (a.getStatus() != null && a.getStatus().equalsIgnoreCase("Ativo") && a.getDataVencimento() != null) {
                cal.setTime(a.getDataVencimento());
                int m = cal.get(java.util.Calendar.MONTH) + 1; // Janeiro = 0
                int y = cal.get(java.util.Calendar.YEAR);

                if (m == mes && y == ano) {
                    resultado.add(a);
                }
            }
        }
        return resultado;
    }

    private void calcularTotais(List<Assinatura> assinaturas) {
        this.totalGasto = 0;
        this.custoPlanejadoMensal = 0;
        this.gastosPorCategoria.clear();

        for (Assinatura a : assinaturas) {
            this.totalGasto += a.getValor();
            this.custoPlanejadoMensal += a.getCustoMensalEquivalente();

            String cat = a.getCategoria() != null ? a.getCategoria().getNome() : "Sem Categoria";
            gastosPorCategoria.put(cat, gastosPorCategoria.getOrDefault(cat, 0.0) + a.getValor());
        }
    }

    public void gerarRelatorio() {
        System.out.println("\n=== RELATORIO FINANCEIRO " + mesReferencia + "/" + anoReferencia + " ===");
        if (!dadosEncontrados) {
            System.out.println("Status: Nao foram encontrados dados para este periodo.");
            return;
        }

        System.out.println("Total Desembolsado no Mes: R$" + String.format("%.2f", totalGasto));
        System.out.println("Custo Medio Mensal Planejado: R$" + String.format("%.2f", custoPlanejadoMensal));
        System.out.println("--- Gastos por Categoria ---");
        for (Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
            System.out.println("  " + entry.getKey() + ": R$" + String.format("%.2f", entry.getValue()));
        }
        System.out.println("=====================================\n");
    }

    public void exportarRelatorio(String formato) {
        System.out.println("[RELATORIO] Exportando para " + formato + "...");
    }

    public void compararPeriodos(RelatorioFinanceiro r1, RelatorioFinanceiro r2) {
        double diff = r1.getTotalGasto() - r2.getTotalGasto();
        System.out.println("[RELATORIO] Variacao de gastos entre os periodos: R$" + String.format("%.2f", diff));
    }

    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMesReferencia() { return mesReferencia; }
    public int getAnoReferencia() { return anoReferencia; }
    public double getTotalGasto() { return totalGasto; }
    public double getCustoPlanejadoMensal() { return custoPlanejadoMensal; }

    public boolean isDadosEncontrados() { return dadosEncontrados; }
}

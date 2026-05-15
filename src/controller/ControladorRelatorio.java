package controller;

import model.Assinatura;
import model.RelatorioFinanceiro;
import model.Usuario;

import java.util.List;

/**
 * Controlador de Relatorios Financeiros.
 * Implementa o fluxo dos diagramas de sequencia e atividade: Relatorio Financeiro.
 *
 * Diagrama de Atividade:
 *   1. Selecionar Gerar Relatorio
 *   2. Informar periodo (Mes/Ano)
 *   3. Buscar dados financeiros
 *   4. Dados encontrados?
 *      - Sim: Calcular totais por categoria -> Exibir Relatorio
 *      - Nao: Avisar "Sem dados no periodo" -> Exibir Relatorio (vazio)
 *   5. Deseja Exportar?
 *      - Sim: Selecionar formato (PDF/CSV) -> Gerar e baixar arquivo
 *      - Nao: Fim
 */
public class ControladorRelatorio {

    /**
     * Fluxo completo do diagrama de atividade: Gerar Relatorio.
     *
     * @param usuario O usuario solicitante
     * @param mes     Mes de referencia
     * @param ano     Ano de referencia
     * @return O relatorio gerado (pode estar vazio se sem dados)
     */
    public RelatorioFinanceiro solicitarRelatorio(Usuario usuario, int mes, int ano) {
        System.out.println("=== [CONTROLADOR RELATORIO] Selecionar Gerar Relatorio ===");
        System.out.println("[CONTROLADOR] Usuario '" + usuario.getNome() + "' informou periodo: " + mes + "/" + ano);

        // Criar relatorio e processar
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro();
        relatorio.setId(usuario.getRelatorios().size() + 1);

        // Delega ao model: buscar dados, verificar, calcular e gerar
        List<Assinatura> assinaturasDoUsuario = usuario.getAssinaturas();
        relatorio.solicitarRelatorio(mes, ano, assinaturasDoUsuario);
        boolean temDados = relatorio.isDadosEncontrados();

        // Associar relatorio ao usuario
        usuario.getRelatorios().add(relatorio);

        if (temDados) {
            System.out.println("[CONTROLADOR] Relatorio com dados gerado e exibido na tela.");
        } else {
            System.out.println("[CONTROLADOR] Relatorio vazio exibido (sem dados no periodo).");
        }

        return relatorio;
    }

    /**
     * Diagrama de atividade: "Deseja Exportar? -> Sim"
     *   -> Selecionar formato (PDF/CSV)
     *   -> Gerar e baixar arquivo
     *
     * @param relatorio O relatorio a exportar
     * @param formato   O formato de exportacao ("PDF" ou "CSV")
     * @return true se exportado com sucesso
     */
    public boolean exportarRelatorio(RelatorioFinanceiro relatorio, String formato) {
        System.out.println("=== [CONTROLADOR RELATORIO] Deseja Exportar? -> Sim ===");

        if (!formato.equalsIgnoreCase("PDF") && !formato.equalsIgnoreCase("CSV")) {
            System.out.println("[CONTROLADOR] Formato invalido: " + formato + ". Use PDF ou CSV.");
            return false;
        }

        System.out.println("[CONTROLADOR] Formato selecionado: " + formato);
        
        StringBuilder sb = new StringBuilder();
        sb.append("RELATORIO FINANCEIRO ID: ").append(relatorio.getId()).append("\n");
        sb.append("Total Gasto: ").append(relatorio.getTotalGasto()).append("\n");
        sb.append("Custo Mensal Planejado: ").append(relatorio.getCustoPlanejadoMensal()).append("\n");
        
        network.PersistenceManager.exportarRelatorioArquivo(sb.toString(), formato);
        
        System.out.println("[CONTROLADOR] Arquivo " + formato + " disponibilizado para download.");
        return true;
    }

    /**
     * Compara dois relatorios financeiros de periodos diferentes.
     */
    public void compararRelatorios(RelatorioFinanceiro r1, RelatorioFinanceiro r2) {
        System.out.println("=== [CONTROLADOR RELATORIO - COMPARACAO] ===");
        r1.compararPeriodos(r1, r2);
    }
}

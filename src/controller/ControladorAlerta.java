package controller;
import model.*;
import network.PersistenceManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Controlador de Alertas (Sistema de Notificacoes).
 * Implementa o fluxo do diagrama de sequencia: Receber Alertas.
 *
 * Fluxo:
 *   1. Timer dispara executarVerificacaoDiaria()
 *   2. Sistema consulta assinaturas vencendo no Banco de Dados
 *   3. [alt] Nenhum vencimento -> registra log
 *   4. [alt] Assinaturas a vencer -> gera notificacoes e envia alerta ao usuario
 */
public class ControladorAlerta {

    private List<LogAcao> logsAlerta;
    private int proximoIdAlerta;

    public ControladorAlerta() {
        this.logsAlerta = new ArrayList<>();
        this.proximoIdAlerta = 1;
    }

    /**
     * Executa a verificacao diaria de assinaturas proximas do vencimento.
     * Este metodo seria chamado por um Timer/Scheduler.
     *
     * @param usuarios Lista de todos os usuarios do sistema
     * @param diasAntecedencia Quantos dias antes do vencimento gerar alerta
     */
    public void executarVerificacaoDiaria(List<Usuario> usuarios, int diasAntecedencia) {
        System.out.println("=== [SISTEMA ALERTAS] Verificacao Diaria Iniciada ===");
        System.out.println("[TIMER] executarVerificacaoDiaria() disparado em: " + new Date());

        for (Usuario usuario : usuarios) {
            System.out.println("\n[SISTEMA] Verificando usuario: " + usuario.getNome());

            // Consultar assinaturas vencendo
            List<Assinatura> vencendo = consultarAssinaturasVencendo(usuario.getAssinaturas(), diasAntecedencia);

            if (vencendo.isEmpty()) {
                // alt [Nenhum vencimento]
                nenhumResultado(usuario);
                registrarLog("Nenhuma assinatura proxima do vencimento para: " + usuario.getNome());
            } else {
                // alt [Assinaturas a vencer]
                List<Alerta> notificacoes = gerarNotificacoes(vencendo, diasAntecedencia);
                enviarAlertaUsuario(usuario, notificacoes);
            }
        }

        System.out.println("\n=== [SISTEMA ALERTAS] Verificacao Diaria Concluida ===");
    }

    /**
     * Consulta no "banco de dados" quais assinaturas estao proximas do vencimento.
     * Retorna a lista de assinaturas que vencem nos proximos N dias.
     *
     * @param assinaturas Lista de assinaturas para verificar
     * @param diasAntecedencia Dias de antecedencia para considerar
     * @return Lista de assinaturas proximas do vencimento
     */
    public List<Assinatura> consultarAssinaturasVencendo(List<Assinatura> assinaturas, int diasAntecedencia) {
        System.out.println("[SISTEMA] Consultando assinaturas vencendo no Banco de Dados...");
        List<Assinatura> vencendo = new ArrayList<>();
        Date agora = new Date();

        for (Assinatura a : assinaturas) {
            if (a.getDataVencimento() != null && a.getStatus() != null
                    && a.getStatus().equalsIgnoreCase("Ativo")) {
                long diffMs = a.getDataVencimento().getTime() - agora.getTime();
                long diffDias = diffMs / (1000 * 60 * 60 * 24);

                if (diffDias >= 0 && diffDias <= diasAntecedencia) {
                    vencendo.add(a);
                }
            }
        }

        System.out.println("[BD] retornarListaVencimentos(): " + vencendo.size() + " assinatura(s) encontrada(s).");
        return vencendo;
    }

    /**
     * Executado quando nenhuma assinatura esta proxima do vencimento.
     */
    public void nenhumResultado(Usuario usuario) {
        System.out.println("[SISTEMA] Nenhum vencimento proximo para " + usuario.getNome() + ". Nenhuma acao necessaria.");
    }

    /**
     * Registra um log da verificacao de alertas.
     */
    public void registrarLog(String mensagem) {
        LogAcao log = new LogAcao(
            logsAlerta.size() + 1,
            mensagem,
            new Date(),
            "sistema-alertas"
        );
        log.registrarLog();
        PersistenceManager.registrarLogLocal(mensagem);
        logsAlerta.add(log);
    }

    /**
     * Gera notificacoes (objetos Alerta) para as assinaturas proximas do vencimento.
     *
     * @param assinaturasVencendo Lista de assinaturas que estao para vencer
     * @param diasAntecedencia Dias de antecedencia configurado
     * @return Lista de alertas gerados
     */
    public List<Alerta> gerarNotificacoes(List<Assinatura> assinaturasVencendo, int diasAntecedencia) {
        System.out.println("[SISTEMA] gerarNotificacoes() - Criando alertas para " + assinaturasVencendo.size() + " assinatura(s)...");
        List<Alerta> alertasGerados = new ArrayList<>();

        for (Assinatura a : assinaturasVencendo) {
            long diffMs = a.getDataVencimento().getTime() - new Date().getTime();
            long diffDias = diffMs / (1000 * 60 * 60 * 24);

            String mensagem = "A assinatura '" + a.getNomeServico()
                + "' (R$" + String.format("%.2f", a.getValor()) + ") vence em " + diffDias + " dia(s)!";

            Alerta alerta = new Alerta(proximoIdAlerta++, mensagem, diasAntecedencia, new Date(), false);
            alerta.dispararAlerta();
            alertasGerados.add(alerta);

            // Associar alerta a assinatura tambem
            a.getAlertas().add(alerta);
        }

        return alertasGerados;
    }

    /**
     * Envia os alertas gerados para o usuario.
     *
     * @param usuario O usuario destinatario
     * @param alertas Lista de alertas a enviar
     */
    public void enviarAlertaUsuario(Usuario usuario, List<Alerta> alertas) {
        System.out.println("[SISTEMA] enviarAlertaUsuario() -> Enviando " + alertas.size() + " alerta(s) para " + usuario.getNome());

        for (Alerta alerta : alertas) {
            usuario.getAlertas().add(alerta);
        }

        registrarLog("Alertas enviados para " + usuario.getNome() + ": " + alertas.size() + " notificacao(oes).");
        System.out.println("[SISTEMA] Alertas entregues com sucesso a " + usuario.getNome() + ".");
    }

    // Getters
    public List<LogAcao> getLogsAlerta() {
        return logsAlerta;
    }
}

package controller;
import model.*;
import network.PersistenceManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador de Assinaturas.
 * Implementa os fluxos dos diagramas de sequencia e atividade:
 *   - Painel Central (com branch "Existem assinaturas? Nao -> Sugerir Cadastro")
 *   - Cadastrar Assinatura (com loop de retentativa em caso de dados invalidos)
 */
public class ControladorAssinatura {

    // Simula um "banco de dados" em memoria de assinaturas
    private List<Assinatura> todasAssinaturas;
    private List<LogAcao> logs;

    public ControladorAssinatura() {
        this.todasAssinaturas = PersistenceManager.carregarAssinaturas();
        this.logs = new ArrayList<>();
    }

    // ================================================================
    //  DIAGRAMA DE ATIVIDADE: Painel Central
    //
    //  Fluxo:
    //    1. Acessar Painel Central
    //    2. Buscar assinaturas no banco
    //    3. Existem assinaturas?
    //       - Sim: (fork paralelo)
    //           -> Calcular proximos vencimentos
    //           -> Compilar historico de pagamentos
    //         (join) -> Exibir Painel Consolidado
    //       - Nao:
    //           -> Exibir mensagem "Nenhuma assinatura"
    //           -> Sugerir Cadastro
    //           -> Exibir Painel Consolidado
    // ================================================================

    /**
     * Fluxo completo do Painel Central conforme diagrama de atividade.
     *
     * @param usuarioId ID do usuario
     * @param assinaturasDoUsuario Assinaturas associadas ao usuario
     * @return Resultado do painel (objeto com dados consolidados)
     */
    public ResultadoPainel acessarPainelCentral(int usuarioId, List<Assinatura> assinaturasDoUsuario) {
        System.out.println("=== [PAINEL CENTRAL] Acessar Painel Central ===");

        // Buscar assinaturas no banco
        List<Assinatura> ativas = buscarAssinaturasNoBanco(usuarioId, assinaturasDoUsuario);

        ResultadoPainel resultado = new ResultadoPainel();

        // Decisao: Existem assinaturas?
        if (ativas.isEmpty()) {
            // Nao -> Exibir mensagem "Nenhuma assinatura" -> Sugerir Cadastro
            exibirMensagemNenhumaAssinatura();
            sugerirCadastro();
            resultado.setSemAssinaturas(true);
            resultado.setMensagem("Nenhuma assinatura encontrada. Cadastre sua primeira assinatura!");
        } else {
            // Sim -> Fork paralelo: calcular vencimentos + compilar historico
            System.out.println("[PAINEL] Assinaturas encontradas! Processando em paralelo...");

            Map<String, Object> vencimentos = calcularVencimentos(ativas);
            List<String> historico = compilarHistorico(ativas);

            // Join -> preparar dados consolidados
            resultado.setSemAssinaturas(false);
            resultado.setAssinaturasAtivas(ativas);
            resultado.setVencimentos(vencimentos);
            resultado.setHistorico(historico);
        }

        // Exibir Painel Consolidado (sempre executa)
        exibirPainelConsolidado(resultado);

        return resultado;
    }

    /**
     * Busca assinaturas ativas do usuario no banco de dados.
     */
    private List<Assinatura> buscarAssinaturasNoBanco(int usuarioId, List<Assinatura> assinaturasDoUsuario) {
        System.out.println("[PAINEL] Buscando assinaturas no banco para usuario ID: " + usuarioId + "...");
        List<Assinatura> ativas = new ArrayList<>();

        for (Assinatura a : assinaturasDoUsuario) {
            if (a.getStatus() != null && a.getStatus().equalsIgnoreCase("Ativo")) {
                ativas.add(a);
            }
        }

        System.out.println("[PAINEL] " + ativas.size() + " assinatura(s) encontrada(s).");
        return ativas;
    }

    /**
     * Diagrama de atividade: "Exibir mensagem: Nenhuma assinatura"
     */
    private void exibirMensagemNenhumaAssinatura() {
        System.out.println("[PAINEL] *** Nenhuma assinatura encontrada para este usuario. ***");
    }

    /**
     * Diagrama de atividade: "Sugerir Cadastro"
     */
    private void sugerirCadastro() {
        System.out.println("[PAINEL] Sugestao: Cadastre sua primeira assinatura para comecar a gerenciar seus gastos!");
    }

    /**
     * Diagrama de atividade: "Exibir Painel Consolidado"
     */
    private void exibirPainelConsolidado(ResultadoPainel resultado) {
        System.out.println("\n[PAINEL] === Exibindo Painel Consolidado ===");
        if (resultado.isSemAssinaturas()) {
            System.out.println("[PAINEL] " + resultado.getMensagem());
        } else {
            System.out.println("[PAINEL] Assinaturas ativas: " + resultado.getAssinaturasAtivas().size());
            System.out.println("[PAINEL] Vencimentos calculados: " + resultado.getVencimentos().size());
            System.out.println("[PAINEL] Registros no historico: " + resultado.getHistorico().size());
        }
        System.out.println("[PAINEL] === Painel Consolidado Exibido ===");
    }

    /**
     * Metodo legado mantido para compatibilidade com diagrama de sequencia.
     */
    public List<Assinatura> getAssinaturasAtivas(int usuarioId, List<Assinatura> assinaturasDoUsuario) {
        ResultadoPainel resultado = acessarPainelCentral(usuarioId, assinaturasDoUsuario);
        return resultado.isSemAssinaturas() ? new ArrayList<>() : resultado.getAssinaturasAtivas();
    }

    /**
     * Consulta assinaturas do usuario (filtra apenas ativas).
     */
    public List<Assinatura> consultarAssinaturas(int usuarioId, List<Assinatura> assinaturasDoUsuario) {
        return buscarAssinaturasNoBanco(usuarioId, assinaturasDoUsuario);
    }

    /**
     * Requisito 2.3: Visao geral do sistema para Administradores.
     * Retorna todas as assinaturas cadastradas no sistema, independente do usuario.
     */
    public List<Assinatura> visaoGeralSistema(Administrador admin) {
        System.out.println("[ADMIN] Administrador '" + admin.getNome() + "' solicitou visao geral do sistema.");
        return todasAssinaturas;
    }

    /**
     * Calcula os proximos vencimentos das assinaturas.
     */
    public Map<String, Object> calcularVencimentos(List<Assinatura> assinaturas) {
        System.out.println("[PAINEL] Calculando proximos vencimentos...");
        Map<String, Object> vencimentos = new HashMap<>();
        Date agora = new Date();

        for (Assinatura a : assinaturas) {
            if (a.getDataVencimento() != null) {
                long diffMs = a.getDataVencimento().getTime() - agora.getTime();
                long diffDias = diffMs / (1000 * 60 * 60 * 24);

                vencimentos.put(a.getNomeServico(), diffDias);

                if (diffDias <= 3 && diffDias >= 0) {
                    System.out.println("  [!] " + a.getNomeServico() + " vence em " + diffDias + " dia(s)!");
                } else if (diffDias < 0) {
                    System.out.println("  [VENCIDO] " + a.getNomeServico() + " venceu ha " + Math.abs(diffDias) + " dia(s).");
                } else {
                    System.out.println("  [OK] " + a.getNomeServico() + " vence em " + diffDias + " dia(s).");
                }
            }
        }

        return vencimentos;
    }

    /**
     * Compila o historico de pagamentos do usuario.
     */
    public List<String> compilarHistorico(List<Assinatura> assinaturas) {
        System.out.println("[PAINEL] Compilando historico de pagamentos...");
        List<String> historico = new ArrayList<>();

        for (Assinatura a : assinaturas) {
            String entrada = a.getNomeServico() + " | R$" + String.format("%.2f", a.getValor())
                + " | Status: " + a.getStatus()
                + " | Categoria: " + (a.getCategoria() != null ? a.getCategoria().getNome() : "N/A");
            historico.add(entrada);
            System.out.println("  -> " + entrada);
        }

        System.out.println("[PAINEL] Historico compilado: " + historico.size() + " registros.");
        return historico;
    }

    // ================================================================
    //  DIAGRAMA: Gerenciar Assinaturas (Edicao e Exclusao)
    // ================================================================

    /**
     * Fluxo: selecionarAssinatura(id) -> buscarAssinatura(id)
     */
    public Assinatura selecionarAssinatura(int id, List<Assinatura> banco) {
        System.out.println("[CONTROLADOR] Selecionar Assinatura ID: " + id);
        return Assinatura.buscarAssinatura(id, banco);
    }

    /**
     * Fluxo: alt [Editar Assinatura] -> editarDados -> update -> confirmacao
     */
    public boolean editarDados(int id, Assinatura novosDados, List<Assinatura> banco) {
        System.out.println("[CONTROLADOR] Editar Dados Assinatura ID: " + id);
        Assinatura alvo = selecionarAssinatura(id, banco);
        
        if (alvo != null) {
            boolean sucesso = alvo.editarAssinatura(novosDados);
            if (sucesso) {
                // Sincronizar com a lista global se nao for a mesma
                if (banco != todasAssinaturas && !todasAssinaturas.contains(alvo)) {
                   // Alvo ja deve estar em todasAssinaturas se foi carregado corretamente
                }
                PersistenceManager.salvarAssinaturas(todasAssinaturas);
                PersistenceManager.salvarLog("Assinatura editada: ID " + id);
                System.out.println("[SISTEMA] exibirConfirmacao(): Alteracoes salvas com sucesso.");
                return true;
            }
        }
        return false;
    }

    /**
     * Fluxo: alt [Excluir Assinatura] -> excluirAssinatura -> delete -> confirmacao
     */
    public boolean excluirAssinatura(int id, List<Assinatura> banco) {
        System.out.println("[CONTROLADOR] Excluir Assinatura ID: " + id);
        Assinatura alvo = selecionarAssinatura(id, banco);
        
        if (alvo != null) {
            boolean sucesso = alvo.excluirAssinatura(id);
            if (sucesso) {
                banco.remove(alvo);
                todasAssinaturas.remove(alvo); // Remove da lista global tambem
                PersistenceManager.salvarAssinaturas(todasAssinaturas);
                PersistenceManager.salvarLog("Assinatura excluida: ID " + id);
                System.out.println("[SISTEMA] exibirConfirmacao(): Assinatura removida com sucesso.");
                return true;
            }
        }
        return false;
    }

    // ================================================================
    //  DIAGRAMA: Organizar por Categoria
    // ================================================================

    /**
     * Fluxo: organizarPorCategoria() -> buscarCategorias() -> agruparPorCategoria()
     */
    public Map<String, List<Assinatura>> organizarPorCategoria(List<Assinatura> assinaturas) {
        System.out.println("[CONTROLADOR] Organizar Assinaturas por Categoria...");
        
        // Simula buscar categorias do banco (select categorias)
        System.out.println("[BD] select categorias...");
        
        // Agrupar
        Map<String, List<Assinatura>> agrupada = agruparPorCategoria(assinaturas);
        
        System.out.println("[SISTEMA] exibirAssinaturasOrganizadas(): Painel atualizado.");
        return agrupada;
    }

    /**
     * Logica interna de agrupamento.
     */
    private Map<String, List<Assinatura>> agruparPorCategoria(List<Assinatura> assinaturas) {
        System.out.println("[CONTROLADOR] agruparPorCategoria() processando...");
        Map<String, List<Assinatura>> mapa = new HashMap<>();

        for (Assinatura a : assinaturas) {
            String cat = (a.getCategoria() != null) ? a.getCategoria().getNome() : "Sem Categoria";
            mapa.computeIfAbsent(cat, k -> new ArrayList<>()).add(a);
        }

        return mapa;
    }

    // ================================================================
    //  DIAGRAMA DE ATIVIDADE: Cadastrar Assinatura
    //
    //  Fluxo:
    //    1. Selecionar Cadastrar Assinatura
    //    2. Solicitar Dados -> Preencher Formulario
    //    3. Dados Validos?
    //       - Sim: Salvar no Banco -> Fim
    //       - Nao: Exibir Mensagem de Erro -> Retornar Para Correcao (loop)
    // ================================================================

    /**
     * Cadastro de assinatura com loop de retentativa.
     * Se os dados forem invalidos, permite corrigir e tentar novamente.
     *
     * @param assinatura A assinatura a ser cadastrada
     * @param maxTentativas Numero maximo de tentativas
     * @return true se cadastrada com sucesso, false se excedeu tentativas
     */
    public boolean cadastrarAssinatura(Assinatura assinatura, int maxTentativas) {
        System.out.println("=== [CADASTRO] Selecionar Cadastrar Assinatura ===");
        int tentativa = 0;

        while (tentativa < maxTentativas) {
            tentativa++;
            System.out.println("\n[CADASTRO] Tentativa " + tentativa + "/" + maxTentativas);
            System.out.println("[CADASTRO] Solicitar Dados -> Preencher Formulario");

            // Decisao: Dados Validos?
            if (assinatura.validarDados()) {
                // Verificar se ID ja existe para evitar duplicatas (Persistencia Inteligente)
                boolean jaExiste = false;
                for (Assinatura a : todasAssinaturas) {
                    if (a.getId() == assinatura.getId()) {
                        jaExiste = true;
                        break;
                    }
                }

                if (jaExiste) {
                    System.out.println("[CADASTRO] Aviso: Assinatura com ID " + assinatura.getId() + " ja existe. Pulando cadastro.");
                    return true; 
                }

                // Sim -> Salvar no Banco
                System.out.println("[CADASTRO] Dados validos! Salvando no banco...");
                boolean sucesso = assinatura.insertAssinatura();

                if (sucesso) {
                    assinatura.confirmacaoCadastro();
                    todasAssinaturas.add(assinatura);
                    PersistenceManager.salvarAssinaturas(todasAssinaturas);
                    PersistenceManager.salvarLog("Assinatura cadastrada: " + assinatura.getNomeServico() + " (ID: " + assinatura.getId() + ")");

                    // Registrar log
                    LogAcao log = new LogAcao(
                        logs.size() + 1,
                        "Assinatura cadastrada: " + assinatura.getNomeServico(),
                        new Date(),
                        "sistema"
                    );
                    log.registrarLog();
                    logs.add(log);

                    System.out.println("[CADASTRO] Cadastro concluido com sucesso!");
                    return true;
                }
            }

            // Nao -> Exibir Mensagem de Erro
            exibirMensagemErroCadastro(assinatura);

            if (tentativa < maxTentativas) {
                // Retornar Para Correcao (loop continua)
                System.out.println("[CADASTRO] Retornando para correcao...");
                retornarParaCorrecao(assinatura);
            }
        }

        System.out.println("[CADASTRO] Numero maximo de tentativas excedido. Cadastro cancelado.");
        return false;
    }

    /**
     * Versao simplificada (tentativa unica) para compatibilidade.
     */
    public boolean cadastrarAssinatura(Assinatura assinatura) {
        return cadastrarAssinatura(assinatura, 1);
    }

    /**
     * Diagrama de atividade: "Exibir Mensagem de Erro"
     */
    private void exibirMensagemErroCadastro(Assinatura assinatura) {
        System.out.println("[CADASTRO] *** ERRO: Dados invalidos para a assinatura. ***");
        System.out.println("[CADASTRO] Verifique: nome do servico, valor, data de vencimento e status.");
    }

    /**
     * Diagrama de atividade: "Retornar Para Correcao"
     * Simula a correcao automatica dos dados para fins de teste.
     */
    private void retornarParaCorrecao(Assinatura assinatura) {
        System.out.println("[CADASTRO] Corrigindo dados automaticamente (simulacao)...");

        // Correcoes automaticas para simular o loop
        if (assinatura.getNomeServico() == null || assinatura.getNomeServico().trim().isEmpty()) {
            assinatura.setNomeServico("Servico Corrigido");
            System.out.println("  -> Nome corrigido para: 'Servico Corrigido'");
        }
        if (assinatura.getValor() <= 0) {
            assinatura.setValor(29.90);
            System.out.println("  -> Valor corrigido para: R$29.90");
        }
        if (assinatura.getDataVencimento() == null) {
            assinatura.setDataVencimento(new Date());
            System.out.println("  -> Data de vencimento corrigida para: " + new Date());
        }
        if (assinatura.getStatus() == null || assinatura.getStatus().trim().isEmpty()) {
            assinatura.setStatus("Ativo");
            System.out.println("  -> Status corrigido para: 'Ativo'");
        }
    }

    // ================================================================
    //  Classe interna: ResultadoPainel
    // ================================================================

    /**
     * Classe que encapsula os dados do Painel Consolidado.
     */
    public static class ResultadoPainel {
        private boolean semAssinaturas;
        private String mensagem;
        private List<Assinatura> assinaturasAtivas;
        private Map<String, Object> vencimentos;
        private List<String> historico;

        public ResultadoPainel() {
            this.assinaturasAtivas = new ArrayList<>();
            this.vencimentos = new HashMap<>();
            this.historico = new ArrayList<>();
        }

        public boolean isSemAssinaturas() { return semAssinaturas; }
        public void setSemAssinaturas(boolean semAssinaturas) { this.semAssinaturas = semAssinaturas; }

        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }

        public List<Assinatura> getAssinaturasAtivas() { return assinaturasAtivas; }
        public void setAssinaturasAtivas(List<Assinatura> assinaturasAtivas) { this.assinaturasAtivas = assinaturasAtivas; }

        public Map<String, Object> getVencimentos() { return vencimentos; }
        public void setVencimentos(Map<String, Object> vencimentos) { this.vencimentos = vencimentos; }

        public List<String> getHistorico() { return historico; }
        public void setHistorico(List<String> historico) { this.historico = historico; }
    }

    // Getters
    public List<Assinatura> getTodasAssinaturas() {
        return todasAssinaturas;
    }

    public void setTodasAssinaturas(List<Assinatura> todasAssinaturas) {
        this.todasAssinaturas = todasAssinaturas;
    }

    public List<LogAcao> getLogs() {
        return logs;
    }
}

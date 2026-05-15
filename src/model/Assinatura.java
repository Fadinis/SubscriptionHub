package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

public class Assinatura {
    private int id;
    private String nomeServico;
    private double valor;
    private Date dataVencimento;
    private String status;
    private Categoria categoria;
    private List<Alerta> alertas;
    private int usuarioId; // Associacao com o proprietario
    private Periodicidade periodicidade;

    public Assinatura() {
        this.alertas = new ArrayList<>();
        this.periodicidade = Periodicidade.MENSAL; // Padrao mensal
    }

    public Assinatura(int id, String nomeServico, double valor, Date dataVencimento, String status, Categoria categoria) {
        this();
        this.id = id;
        this.nomeServico = nomeServico;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.categoria = categoria;
    }

    public Assinatura(int id, String nomeServico, double valor, Date dataVencimento, String status, Categoria categoria, Periodicidade periodicidade, int usuarioId) {
        this(id, nomeServico, valor, dataVencimento, status, categoria);
        this.periodicidade = periodicidade;
        this.usuarioId = usuarioId;
    }

    /**
     * Calcula e atualiza a data de vencimento para o proximo periodo.
     * Ex: Se for mensal, soma 1 mes. Se for anual, soma 1 ano.
     */
    public void renovarAssinatura() {
        if (dataVencimento == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataVencimento);
        cal.add(Calendar.MONTH, periodicidade.getMeses());
        
        this.dataVencimento = cal.getTime();
        System.out.println("[SISTEMA] Assinatura " + nomeServico + " renovada automaticamente.");
        System.out.println("  -> Novo vencimento (" + periodicidade.getDescricao() + "): " + dataVencimento);
    }

    /**
     * Retorna o valor mensal equivalente para planejamento financeiro.
     */
    public double getCustoMensalEquivalente() {
        return valor / periodicidade.getMeses();
    }

    // --- Metodos do Diagrama de Sequencia: Gerenciar Assinaturas ---

    /**
     * Simula busca no banco de dados.
     */
    public static Assinatura buscarAssinatura(int id, List<Assinatura> banco) {
        System.out.println("[BD] select assinatura where id = " + id);
        for (Assinatura a : banco) {
            if (a.getId() == id) {
                System.out.println("[BD] dadosAssinatura retornados.");
                return a;
            }
        }
        return null;
    }

    /**
     * Simula update no banco de dados.
     */
    public boolean editarAssinatura(Assinatura novosDados) {
        System.out.println("[BD] update assinatura set campos = novosDados where id = " + this.id);
        this.nomeServico = novosDados.getNomeServico();
        this.valor = novosDados.getValor();
        this.dataVencimento = novosDados.getDataVencimento();
        this.status = novosDados.getStatus();
        this.categoria = novosDados.getCategoria();
        this.periodicidade = novosDados.getPeriodicidade();
        System.out.println("[BD] sucesso: registro atualizado.");
        return true;
    }

    /**
     * Simula delete no banco de dados.
     */
    public boolean excluirAssinatura(int id) {
        System.out.println("[BD] delete assinatura where id = " + id);
        System.out.println("[BD] sucesso: registro removido.");
        return true;
    }

    // --- Metodos do Diagrama de Sequencia: Cadastrar Assinatura ---

    public boolean validarDados() {
        if (nomeServico == null || nomeServico.trim().isEmpty()) {
            System.out.println("[VALIDACAO] Erro: Nome do servico e obrigatorio.");
            return false;
        }
        if (valor <= 0) {
            System.out.println("[VALIDACAO] Erro: Valor deve ser maior que zero.");
            return false;
        }
        if (dataVencimento == null) {
            System.out.println("[VALIDACAO] Erro: Data de vencimento e obrigatoria.");
            return false;
        }
        return true;
    }

    public boolean insertAssinatura() {
        System.out.println("[BD] Inserindo assinatura " + periodicidade.getDescricao() + ": " + nomeServico);
        return true;
    }

    public String confirmacaoCadastro() {
        return "Assinatura " + periodicidade.getDescricao() + " '" + nomeServico + "' cadastrada!";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public Date getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(Date dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<Alerta> getAlertas() { return alertas; }
    public void setAlertas(List<Alerta> alertas) { this.alertas = alertas; }

    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) { this.periodicidade = periodicidade; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}

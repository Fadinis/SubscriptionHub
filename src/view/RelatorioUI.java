package view;

import controller.ControladorRelatorio;
import model.RelatorioFinanceiro;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Map;

/**
 * Interface Gráfica de Relatórios Financeiros (Dark Mode Premium).
 */
public class RelatorioUI extends JDialog {

    private ControladorRelatorio ctrlRelatorio;
    private Usuario usuarioLogado;
    private RelatorioFinanceiro relatorioAtual;

    // Componentes de UI
    private JComboBox<Integer> cbMes;
    private JComboBox<Integer> cbAno;
    private JLabel lblTotalGasto;
    private JLabel lblCustoMensalEquiv;
    private JPanel panelCategorias;
    private JLabel lblComparacao;
    private JButton btnExportarPDF;
    private JButton btnExportarCSV;
    private JPanel cardResultado;
    private JLabel lblStatusRelatorio;

    // Cores Premium
    private static final Color COLOR_BG = new Color(18, 18, 20);
    private static final Color COLOR_CARD = new Color(29, 29, 34);
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color COLOR_PRIMARY_HOVER = new Color(79, 70, 229);
    private static final Color COLOR_ACCENT_GREEN = new Color(16, 185, 129); // Success Green
    private static final Color COLOR_FIELD_BG = new Color(42, 42, 48);
    private static final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private static final Color COLOR_TEXT_MUTED = new Color(156, 163, 175);
    private static final Color COLOR_BORDER = new Color(63, 63, 70);

    public RelatorioUI(Frame parent, Usuario usuarioLogado, ControladorRelatorio ctrlRelatorio) {
        super(parent, "Relatórios Financeiros", true);
        this.usuarioLogado = usuarioLogado;
        this.ctrlRelatorio = ctrlRelatorio;

        configurarDialog();
        inicializarComponentes();
        gerarRelatorioAutomatico();
        setLocationRelativeTo(parent);
    }

    private void configurarDialog() {
        setSize(520, 680);
        setResizable(true);
        setMinimumSize(new Dimension(520, 680));
        getContentPane().setBackground(COLOR_BG);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setPreferredSize(new Dimension(460, 600));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        // Título da Tela
        JLabel lblTitulo = new JLabel("Relatório Financeiro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        cardPanel.add(lblTitulo);

        // Painel de Filtros (Mês e Ano)
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelFiltros.setBackground(COLOR_CARD);
        panelFiltros.setMaximumSize(new Dimension(440, 50));

        JLabel lblMes = new JLabel("Mês:");
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMes.setForeground(COLOR_TEXT_MUTED);

        cbMes = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}) {
            @Override
            public void updateUI() {
                UIManager.put("ComboBox.background", COLOR_FIELD_BG);
                UIManager.put("ComboBox.foreground", COLOR_TEXT_PRIMARY);
                UIManager.put("ComboBox.selectionBackground", COLOR_PRIMARY);
                UIManager.put("ComboBox.selectionForeground", Color.WHITE);
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
                setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
                setBackground(COLOR_FIELD_BG);
                setForeground(COLOR_TEXT_PRIMARY);
                setBorder(new LineBorder(COLOR_BORDER, 1));
                setOpaque(true);
            }
        };
        configurarComboBox(cbMes);

        JLabel lblAno = new JLabel("Ano:");
        lblAno.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAno.setForeground(COLOR_TEXT_MUTED);

        cbAno = new JComboBox<>(new Integer[]{2025, 2026, 2027, 2028}) {
            @Override
            public void updateUI() {
                UIManager.put("ComboBox.background", COLOR_FIELD_BG);
                UIManager.put("ComboBox.foreground", COLOR_TEXT_PRIMARY);
                UIManager.put("ComboBox.selectionBackground", COLOR_PRIMARY);
                UIManager.put("ComboBox.selectionForeground", Color.WHITE);
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
                setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
                setBackground(COLOR_FIELD_BG);
                setForeground(COLOR_TEXT_PRIMARY);
                setBorder(new LineBorder(COLOR_BORDER, 1));
                setOpaque(true);
            }
        };
        configurarComboBox(cbAno);
        cbAno.setSelectedItem(2026); // Ano padrão nos testes

        JButton btnGerar = new JButton("Gerar");
        configurarBotao(btnGerar, COLOR_PRIMARY, COLOR_PRIMARY_HOVER, 80, 28);
        btnGerar.addActionListener(e -> processarGerarRelatorio());

        panelFiltros.add(lblMes);
        panelFiltros.add(cbMes);
        panelFiltros.add(lblAno);
        panelFiltros.add(cbAno);
        panelFiltros.add(btnGerar);

        cardPanel.add(panelFiltros);
        cardPanel.add(Box.createVerticalStrut(15));

        // --- PAINEL DE RESULTADOS ---
        cardResultado = new JPanel();
        cardResultado.setBackground(COLOR_FIELD_BG);
        cardResultado.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        cardResultado.setLayout(new BoxLayout(cardResultado, BoxLayout.Y_AXIS));
        cardResultado.setMaximumSize(new Dimension(420, 360));

        lblStatusRelatorio = new JLabel("Relatório gerado com sucesso.");
        lblStatusRelatorio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusRelatorio.setForeground(COLOR_ACCENT_GREEN);
        lblStatusRelatorio.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblStatusRelatorio.setBorder(new EmptyBorder(0, 0, 10, 0));
        cardResultado.add(lblStatusRelatorio);

        // Cards de KPI rápido
        JPanel panelKPI = new JPanel();
        panelKPI.setLayout(new GridLayout(1, 2, 10, 0));
        panelKPI.setBackground(COLOR_FIELD_BG);
        panelKPI.setMaximumSize(new Dimension(400, 65));

        JPanel kpi1 = criarKPICard("TOTAL GASTO NO MÊS", lblTotalGasto = new JLabel("R$ 0,00"));
        JPanel kpi2 = criarKPICard("CUSTO MENSAL PLANEJADO", lblCustoMensalEquiv = new JLabel("R$ 0,00"));
        panelKPI.add(kpi1);
        panelKPI.add(kpi2);

        cardResultado.add(panelKPI);
        cardResultado.add(Box.createVerticalStrut(15));

        // Lista de gastos por categoria
        JLabel lblCatTitulo = new JLabel("Gastos Proporcionais por Categoria:");
        lblCatTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCatTitulo.setForeground(COLOR_TEXT_PRIMARY);
        lblCatTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCatTitulo.setBorder(new EmptyBorder(0, 0, 5, 0));
        cardResultado.add(lblCatTitulo);

        panelCategorias = new JPanel();
        panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));
        panelCategorias.setBackground(COLOR_FIELD_BG);
        JScrollPane scrollCat = new JScrollPane(panelCategorias);
        scrollCat.setBorder(null);
        scrollCat.setBackground(COLOR_FIELD_BG);
        scrollCat.getViewport().setBackground(COLOR_FIELD_BG);
        scrollCat.setMaximumSize(new Dimension(400, 120));
        scrollCat.setPreferredSize(new Dimension(400, 120));
        scrollCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardResultado.add(scrollCat);
        cardResultado.add(Box.createVerticalStrut(15));

        // Módulo de comparação temporal (Mês a Mês)
        JPanel panelComparacao = new JPanel();
        panelComparacao.setLayout(new BorderLayout());
        panelComparacao.setBackground(new Color(23, 23, 27));
        panelComparacao.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        panelComparacao.setMaximumSize(new Dimension(400, 55));
        panelComparacao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCompTitle = new JLabel("Comparação com o Período Anterior:");
        lblCompTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCompTitle.setForeground(COLOR_TEXT_MUTED);

        lblComparacao = new JLabel("Sem dados anteriores para comparação.");
        lblComparacao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblComparacao.setForeground(COLOR_TEXT_PRIMARY);

        panelComparacao.add(lblCompTitle, BorderLayout.NORTH);
        panelComparacao.add(lblComparacao, BorderLayout.CENTER);
        cardResultado.add(panelComparacao);

        cardPanel.add(cardResultado);
        cardPanel.add(Box.createVerticalStrut(20));

        // Seção Exportação
        JLabel lblExportarTitulo = new JLabel("Deseja Exportar este Relatório?");
        lblExportarTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblExportarTitulo.setForeground(COLOR_TEXT_MUTED);
        lblExportarTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblExportarTitulo.setBorder(new EmptyBorder(0, 0, 8, 0));
        cardPanel.add(lblExportarTitulo);

        JPanel panelExportBotoes = new JPanel(new GridLayout(1, 2, 15, 0));
        panelExportBotoes.setBackground(COLOR_CARD);
        panelExportBotoes.setMaximumSize(new Dimension(360, 40));
        panelExportBotoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnExportarPDF = new JButton("Exportar em PDF");
        configurarBotao(btnExportarPDF, COLOR_ACCENT_GREEN, COLOR_ACCENT_GREEN.darker(), 160, 36);
        btnExportarPDF.addActionListener(e -> processarExportacao("PDF"));

        btnExportarCSV = new JButton("Exportar em CSV");
        configurarBotao(btnExportarCSV, new Color(59, 130, 246), new Color(29, 78, 216), 160, 36);
        btnExportarCSV.addActionListener(e -> processarExportacao("CSV"));

        panelExportBotoes.add(btnExportarPDF);
        panelExportBotoes.add(btnExportarCSV);
        cardPanel.add(panelExportBotoes);

        add(cardPanel);
    }

    private void configurarComboBox(JComboBox<Integer> cb) {
        cb.setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setPreferredSize(new Dimension(75, 26));
    }

    private void configurarBotao(JButton btn, Color corFundo, Color corHover, int width, int height) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(corFundo);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setMaximumSize(new Dimension(width, height));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(corHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(corFundo);
            }
        });
    }

    private JPanel criarKPICard(String labelText, JLabel lblValor) {
        JPanel kpi = new JPanel();
        kpi.setBackground(new Color(23, 23, 27));
        kpi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        kpi.setLayout(new BoxLayout(kpi, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(labelText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 8));
        title.setForeground(COLOR_TEXT_MUTED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValor.setForeground(COLOR_TEXT_PRIMARY);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValor.setBorder(new EmptyBorder(3, 0, 0, 0));

        kpi.add(title);
        kpi.add(lblValor);
        return kpi;
    }

    private void gerarRelatorioAutomatico() {
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH) + 1;
        cbMes.setSelectedItem(mes);
        processarGerarRelatorio();
    }

    private void processarGerarRelatorio() {
        int mes = (Integer) cbMes.getSelectedItem();
        int ano = (Integer) cbAno.getSelectedItem();

        // 1. Chama o controlador de relatórios (Gera e exibe)
        relatorioAtual = ctrlRelatorio.solicitarRelatorio(usuarioLogado, mes, ano);

        // 2. Atualiza a UI com os dados do relatório
        lblTotalGasto.setText("R$ " + String.format("%.2f", relatorioAtual.getTotalGasto()));
        lblCustoMensalEquiv.setText("R$ " + String.format("%.2f", relatorioAtual.getCustoPlanejadoMensal()));

        panelCategorias.removeAll();
        
        if (relatorioAtual.isDadosEncontrados()) {
            lblStatusRelatorio.setText("Dados financeiros calculados para " + mes + "/" + ano);
            lblStatusRelatorio.setForeground(COLOR_ACCENT_GREEN);
            btnExportarPDF.setEnabled(true);
            btnExportarCSV.setEnabled(true);

            // Popula gastos por categoria
            for (Map.Entry<String, Double> entry : relatorioAtual.getGastosPorCategoria().entrySet()) {
                JPanel item = new JPanel(new BorderLayout());
                item.setBackground(COLOR_FIELD_BG);
                item.setBorder(new EmptyBorder(3, 5, 3, 5));
                item.setMaximumSize(new Dimension(380, 25));

                JLabel lblNome = new JLabel("  • " + entry.getKey());
                lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblNome.setForeground(COLOR_TEXT_PRIMARY);

                JLabel lblVal = new JLabel("R$ " + String.format("%.2f", entry.getValue()) + "  ");
                lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblVal.setForeground(COLOR_TEXT_PRIMARY);

                item.add(lblNome, BorderLayout.WEST);
                item.add(lblVal, BorderLayout.EAST);
                panelCategorias.add(item);
            }

            JLabel lblAssinaturasTitulo = new JLabel("Assinaturas no Período:");
            lblAssinaturasTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblAssinaturasTitulo.setForeground(COLOR_TEXT_PRIMARY);
            lblAssinaturasTitulo.setBorder(new EmptyBorder(10, 0, 5, 0));
            panelCategorias.add(lblAssinaturasTitulo);

            for (model.Assinatura a : relatorioAtual.getAssinaturasNoPeriodo()) {
                JPanel itemA = new JPanel(new BorderLayout());
                itemA.setBackground(COLOR_FIELD_BG);
                itemA.setBorder(new EmptyBorder(3, 5, 3, 5));
                itemA.setMaximumSize(new Dimension(380, 25));

                JLabel lblNomeA = new JLabel("  - " + a.getNomeServico() + " (" + a.getPeriodicidade().getDescricao() + ")");
                lblNomeA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblNomeA.setForeground(COLOR_TEXT_MUTED);

                JLabel lblValA = new JLabel("R$ " + String.format("%.2f", a.getValor()) + "  ");
                lblValA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblValA.setForeground(COLOR_TEXT_MUTED);

                itemA.add(lblNomeA, BorderLayout.WEST);
                itemA.add(lblValA, BorderLayout.EAST);
                panelCategorias.add(itemA);
            }
        } else {
            lblStatusRelatorio.setText("Sem assinaturas ativas com vencimento neste período.");
            lblStatusRelatorio.setForeground(new Color(239, 68, 68)); // Vermelho Coral
            btnExportarPDF.setEnabled(false);
            btnExportarCSV.setEnabled(false);

            JLabel lblEmpty = new JLabel("Nenhum custo registrado para o período.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblEmpty.setForeground(COLOR_TEXT_MUTED);
            lblEmpty.setBorder(new EmptyBorder(10, 10, 0, 0));
            panelCategorias.add(lblEmpty);
        }

        // 3. Realiza a comparação temporal (Requisito RU-09)
        int mesAnt = (mes == 1) ? 12 : mes - 1;
        int anoAnt = (mes == 1) ? ano - 1 : ano;
        
        RelatorioFinanceiro relatorioAnterior = new RelatorioFinanceiro();
        relatorioAnterior.solicitarRelatorio(mesAnt, anoAnt, usuarioLogado.getAssinaturas());

        if (relatorioAtual.isDadosEncontrados() || relatorioAnterior.isDadosEncontrados()) {
            double diff = relatorioAtual.getTotalGasto() - relatorioAnterior.getTotalGasto();
            if (diff > 0) {
                lblComparacao.setText("Aumento de R$ " + String.format("%.2f", diff) + " (+ gastos) em relação ao mês anterior.");
                lblComparacao.setForeground(new Color(239, 68, 68)); // Vermelho
            } else if (diff < 0) {
                lblComparacao.setText("Economia de R$ " + String.format("%.2f", Math.abs(diff)) + " (- gastos) em relação ao mês anterior.");
                lblComparacao.setForeground(COLOR_ACCENT_GREEN); // Verde
            } else {
                lblComparacao.setText("Gastos idênticos aos do mês anterior (" + mesAnt + "/" + anoAnt + ").");
                lblComparacao.setForeground(COLOR_TEXT_PRIMARY);
            }
        } else {
            lblComparacao.setText("Sem dados anteriores para comparação.");
            lblComparacao.setForeground(COLOR_TEXT_MUTED);
        }

        panelCategorias.revalidate();
        panelCategorias.repaint();
    }

    private void processarExportacao(String formato) {
        if (relatorioAtual == null) return;

        boolean sucesso = ctrlRelatorio.exportarRelatorio(relatorioAtual, formato);
        if (sucesso) {
            UIManager.put("OptionPane.background", COLOR_CARD);
            UIManager.put("Panel.background", COLOR_CARD);
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Button.background", COLOR_PRIMARY);
            UIManager.put("Button.foreground", Color.WHITE);

            JOptionPane.showMessageDialog(this, 
                    "Relatório exportado com sucesso!\nO arquivo foi gerado localmente em formato " + formato + ".\nNome: relatorio_exportado." + formato.toLowerCase(), 
                    "Exportação de Dados", 
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao exportar relatório.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class DarkComboBoxRenderer extends DefaultListCellRenderer {
        private final Color bg;
        private final Color fg;
        private final Color selectionBg;

        public DarkComboBoxRenderer(Color bg, Color fg, Color selectionBg) {
            this.bg = bg;
            this.fg = fg;
            this.selectionBg = selectionBg;
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected && index != -1) {
                c.setBackground(selectionBg);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(bg);
                c.setForeground(fg);
            }
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(new EmptyBorder(5, 10, 5, 10));
            }
            return c;
        }
    }
}

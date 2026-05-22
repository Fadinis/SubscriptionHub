package view;

import controller.ControladorAlerta;
import controller.ControladorAssinatura;
import controller.ControladorRelatorio;
import controller.ControladorUsuario;
import model.Alerta;
import model.Assinatura;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface Gráfica Principal do Painel Central / Dashboard (Dark Mode Premium).
 * Requisitos Implementados: RU-02, RU-03, RU-04, RU-06, RU-07, RU-08, RF-03, RF-04, RF-05.
 */
public class PainelUI extends JFrame {

    private Usuario usuarioLogado;
    private ControladorUsuario ctrlUsuario;
    private ControladorAssinatura ctrlAssinatura;
    private ControladorRelatorio ctrlRelatorio;
    private ControladorAlerta ctrlAlerta;

    // Componentes de UI
    private JLabel lblBoasVindas;
    private JLabel lblTotalGasto;
    private JLabel lblQtdAssinaturas;
    private JLabel lblProximoVencimento;
    private JTable tabelaAssinaturas;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> cbFiltroCategoria;
    private JPanel panelNotificacoes;
    private List<Alerta> alertasAtivos;

    // Cores Premium
    private static final Color COLOR_BG = new Color(18, 18, 20); // Fundo escuro profundo
    private static final Color COLOR_SIDEBAR = new Color(26, 26, 30); // Sidebar
    private static final Color COLOR_CARD = new Color(29, 29, 34); // Cards
    private static final Color COLOR_CARD_ALT = new Color(42, 42, 48); // Listas
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color COLOR_PRIMARY_HOVER = new Color(79, 70, 229);
    private static final Color COLOR_ACCENT_GREEN = new Color(16, 185, 129); // Verde
    private static final Color COLOR_ACCENT_RED = new Color(239, 68, 68); // Vermelho/Laranja
    private static final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private static final Color COLOR_TEXT_MUTED = new Color(156, 163, 175);
    private static final Color COLOR_BORDER = new Color(63, 63, 70);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PainelUI(Usuario usuarioLogado, ControladorUsuario ctrlUsuario, 
                    ControladorAssinatura ctrlAssinatura, ControladorRelatorio ctrlRelatorio, 
                    ControladorAlerta ctrlAlerta) {
        this.usuarioLogado = usuarioLogado;
        this.ctrlUsuario = ctrlUsuario;
        this.ctrlAssinatura = ctrlAssinatura;
        this.ctrlRelatorio = ctrlRelatorio;
        this.ctrlAlerta = ctrlAlerta;
        this.alertasAtivos = new ArrayList<>();

        configurarJanela();
        inicializarComponentes();
        atualizarDadosPainel();
        carregarAlertasIniciais();
        setLocationRelativeTo(null);
    }

    private void configurarJanela() {
        setTitle("Subscription Hub - Dashboard Central");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 580));
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // --- 1. SIDEBAR (PAINEL ESQUERDO) ---
        JPanel panelEsquerdo = new JPanel();
        panelEsquerdo.setBackground(COLOR_SIDEBAR);
        panelEsquerdo.setPreferredSize(new Dimension(220, 680));
        panelEsquerdo.setBorder(new MatteBorder(0, 0, 0, 1, COLOR_BORDER));
        panelEsquerdo.setLayout(new BorderLayout());

        // Header da Sidebar (Info do Usuário)
        JPanel panelUserInfo = new JPanel();
        panelUserInfo.setBackground(COLOR_SIDEBAR);
        panelUserInfo.setBorder(new EmptyBorder(30, 20, 30, 20));
        panelUserInfo.setLayout(new BoxLayout(panelUserInfo, BoxLayout.Y_AXIS));

        JLabel lblAvatar = new JLabel("SH");
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblAvatar.setForeground(COLOR_TEXT_PRIMARY);
        lblAvatar.setBackground(COLOR_PRIMARY);
        lblAvatar.setOpaque(true);
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(60, 60));
        lblAvatar.setMaximumSize(new Dimension(60, 60));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setBorder(new LineBorder(COLOR_BORDER, 2, true));

        lblBoasVindas = new JLabel("Olá, " + usuarioLogado.getNome().split(" ")[0] + "!");
        lblBoasVindas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBoasVindas.setForeground(COLOR_TEXT_PRIMARY);
        lblBoasVindas.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBoasVindas.setBorder(new EmptyBorder(15, 0, 2, 0));

        JLabel lblEmail = new JLabel(usuarioLogado.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEmail.setForeground(COLOR_TEXT_MUTED);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelUserInfo.add(lblAvatar);
        panelUserInfo.add(lblBoasVindas);
        panelUserInfo.add(lblEmail);
        panelEsquerdo.add(panelUserInfo, BorderLayout.NORTH);

        // Menu de Botões da Sidebar
        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(COLOR_SIDEBAR);
        panelMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 12));

        JButton btnRelatorios = criarBotaoSidebar("Relatórios Financeiros");
        btnRelatorios.addActionListener(e -> abrirPainelRelatorios());

        JButton btnSimularAlertas = criarBotaoSidebar("Verificar Alertas");
        btnSimularAlertas.addActionListener(e -> simularVerificacaoAlertas());

        JButton btnLogout = criarBotaoSidebar("Sair da Conta");
        btnLogout.setBackground(new Color(220, 38, 38)); // Vermelho escuro
        btnLogout.addActionListener(e -> processarLogout());

        panelMenu.add(btnRelatorios);
        panelMenu.add(btnSimularAlertas);
        panelMenu.add(btnLogout);
        panelEsquerdo.add(panelMenu, BorderLayout.CENTER);

        add(panelEsquerdo, BorderLayout.WEST);

        // --- 2. PAINEL CENTRAL PRINCIPAL ---
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_BG);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Subpainel Superior (KPIs + Alertas)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(COLOR_BG);

        // KPI Cards Row
        JPanel panelKPIs = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKPIs.setBackground(COLOR_BG);
        panelKPIs.setMaximumSize(new Dimension(800, 110));
        panelKPIs.setPreferredSize(new Dimension(800, 110));

        JPanel cardSpent = criarKPICard("TOTAL EM ASSINATURAS", lblTotalGasto = new JLabel("R$ 0,00"), COLOR_PRIMARY);
        JPanel cardCount = criarKPICard("ASSINATURAS ATIVAS", lblQtdAssinaturas = new JLabel("0"), COLOR_ACCENT_GREEN);
        JPanel cardNext = criarKPICard("PRÓXIMO VENCIMENTO", lblProximoVencimento = new JLabel("Nenhum"), COLOR_ACCENT_RED);

        panelKPIs.add(cardSpent);
        panelKPIs.add(cardCount);
        panelKPIs.add(cardNext);
        panelSuperior.add(panelKPIs);
        panelSuperior.add(Box.createVerticalStrut(20));

        // Central de Notificações / Alertas (RU-04)
        panelNotificacoes = new JPanel();
        panelNotificacoes.setLayout(new BoxLayout(panelNotificacoes, BoxLayout.Y_AXIS));
        panelNotificacoes.setBackground(COLOR_BG);
        panelNotificacoes.setVisible(false); // Oculta se não houver notificações
        panelSuperior.add(panelNotificacoes);
        
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // --- 3. PAINEL DE ASSINATURAS (TABELA + AÇÕES) ---
        JPanel panelConteudo = new JPanel(new BorderLayout());
        panelConteudo.setBackground(COLOR_CARD);
        panelConteudo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header das Assinaturas (Título + Filtro de Categoria)
        JPanel panelHeaderAssinaturas = new JPanel(new BorderLayout());
        panelHeaderAssinaturas.setBackground(COLOR_CARD);
        panelHeaderAssinaturas.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblListTitulo = new JLabel("Minhas Assinaturas");
        lblListTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListTitulo.setForeground(COLOR_TEXT_PRIMARY);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFiltro.setBackground(COLOR_CARD);
        JLabel lblFiltro = new JLabel("Filtrar por Categoria:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltro.setForeground(COLOR_TEXT_MUTED);

        cbFiltroCategoria = new JComboBox<>(new String[]{"Todas"}) {
            @Override
            public void updateUI() {
                UIManager.put("ComboBox.background", COLOR_CARD_ALT);
                UIManager.put("ComboBox.foreground", COLOR_TEXT_PRIMARY);
                UIManager.put("ComboBox.selectionBackground", COLOR_PRIMARY);
                UIManager.put("ComboBox.selectionForeground", Color.WHITE);
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
                setRenderer(new DarkComboBoxRenderer(COLOR_CARD_ALT, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
                setBackground(COLOR_CARD_ALT);
                setForeground(COLOR_TEXT_PRIMARY);
                setBorder(new LineBorder(COLOR_BORDER, 1));
                setOpaque(true);
            }
        };
        cbFiltroCategoria.setRenderer(new DarkComboBoxRenderer(COLOR_CARD_ALT, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
        cbFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltroCategoria.setPreferredSize(new Dimension(130, 26));
        cbFiltroCategoria.addActionListener(e -> filtrarAssinaturasTabela());

        panelFiltro.add(lblFiltro);
        panelFiltro.add(cbFiltroCategoria);

        panelHeaderAssinaturas.add(lblListTitulo, BorderLayout.WEST);
        panelHeaderAssinaturas.add(panelFiltro, BorderLayout.EAST);
        panelConteudo.add(panelHeaderAssinaturas, BorderLayout.NORTH);

        // Tabela Estilizada (Dark Mode)
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Serviço", "Custo", "Vencimento", "Categoria", "Periodicidade", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bloqueia edição direta nas células
            }
        };

        tabelaAssinaturas = new JTable(modeloTabela);
        configurarEstiloTabela();

        JScrollPane scrollTabela = new JScrollPane(tabelaAssinaturas);
        scrollTabela.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollTabela.getViewport().setBackground(COLOR_CARD);
        panelConteudo.add(scrollTabela, BorderLayout.CENTER);

        // Ações da Tabela (CRUD)
        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panelAcoes.setBackground(COLOR_CARD);

        JButton btnAdicionar = criarBotaoAcao("Adicionar Assinatura", COLOR_PRIMARY);
        btnAdicionar.addActionListener(e -> abrirTelaCadastroAssinatura());

        JButton btnEditar = criarBotaoAcao("Editar Dados", COLOR_CARD_ALT);
        btnEditar.addActionListener(e -> abrirTelaEdicaoAssinatura());

        JButton btnExcluir = criarBotaoAcao("Excluir Assinatura", COLOR_CARD_ALT);
        btnExcluir.addActionListener(e -> processarExcluirAssinatura());

        JButton btnRenovar = criarBotaoAcao("Confirmar Renovação", COLOR_CARD_ALT);
        btnRenovar.addActionListener(e -> processarRenovacaoManual());

        panelAcoes.add(btnAdicionar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);
        panelAcoes.add(btnRenovar);
        panelConteudo.add(panelAcoes, BorderLayout.SOUTH);

        panelPrincipal.add(panelConteudo, BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JButton criarBotaoSidebar(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(COLOR_CARD);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setMaximumSize(new Dimension(180, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground().equals(COLOR_CARD)) {
                    btn.setBackground(COLOR_CARD_ALT);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground().equals(COLOR_CARD_ALT)) {
                    btn.setBackground(COLOR_CARD);
                }
            }
        });

        return btn;
    }

    private JButton criarBotaoAcao(String text, Color corFundo) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(corFundo);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(corFundo.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(corFundo);
            }
        });

        return btn;
    }

    private JPanel criarKPICard(String titleText, JLabel lblValor, Color corBorda) {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(4, 0, 0, 0, corBorda),
                new EmptyBorder(10, 15, 10, 15)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 10));
        title.setForeground(COLOR_TEXT_MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(COLOR_TEXT_PRIMARY);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblValor.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.add(title);
        card.add(lblValor);
        return card;
    }

    private void configurarEstiloTabela() {
        tabelaAssinaturas.setBackground(COLOR_CARD);
        tabelaAssinaturas.setForeground(COLOR_TEXT_PRIMARY);
        tabelaAssinaturas.setGridColor(COLOR_BORDER);
        tabelaAssinaturas.setSelectionBackground(new Color(79, 70, 229)); // Indigo Escuro
        tabelaAssinaturas.setSelectionForeground(COLOR_TEXT_PRIMARY);
        tabelaAssinaturas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaAssinaturas.setRowHeight(32);
        tabelaAssinaturas.setFillsViewportHeight(true);

        // Remover linhas horizontais e verticais para visual flat
        tabelaAssinaturas.setShowHorizontalLines(true);
        tabelaAssinaturas.setShowVerticalLines(false);

        // Estilo do Cabeçalho
        JTableHeader header = tabelaAssinaturas.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(COLOR_SIDEBAR);
                c.setForeground(COLOR_TEXT_PRIMARY);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 1, COLOR_BORDER),
                        new EmptyBorder(0, 5, 0, 5)
                ));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        header.setPreferredSize(new Dimension(0, 36));

        // Renderizadores personalizados de coluna
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                c.setBackground(row % 2 == 0 ? COLOR_CARD : COLOR_CARD_ALT);
                c.setForeground(COLOR_TEXT_PRIMARY);
                if (isSelected) c.setBackground(new Color(79, 70, 229));
                return c;
            }
        };

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                c.setBackground(row % 2 == 0 ? COLOR_CARD : COLOR_CARD_ALT);
                c.setForeground(COLOR_TEXT_PRIMARY);
                if (isSelected) c.setBackground(new Color(79, 70, 229));
                return c;
            }
        };

        // Renderizador de Status (Destaca em Verde/Vermelho)
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                c.setBackground(row % 2 == 0 ? COLOR_CARD : COLOR_CARD_ALT);
                
                String val = (String) value;
                if (val != null && val.equalsIgnoreCase("Ativo")) {
                    setForeground(COLOR_ACCENT_GREEN);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    setForeground(COLOR_TEXT_MUTED);
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }

                if (isSelected) c.setBackground(new Color(79, 70, 229));
                return c;
            }
        };

        // Aplica os renderizadores
        tabelaAssinaturas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tabelaAssinaturas.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Serviço
        tabelaAssinaturas.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Custo
        tabelaAssinaturas.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Vencimento
        tabelaAssinaturas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Categoria
        tabelaAssinaturas.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Periodicidade
        tabelaAssinaturas.getColumnModel().getColumn(6).setCellRenderer(statusRenderer); // Status
    }

    private void atualizarDadosPainel() {
        // Chamada ao controlador via Diagrama: acessarPainelCentral
        ControladorAssinatura.ResultadoPainel painelInfo = ctrlAssinatura.acessarPainelCentral(
                usuarioLogado.getId(), usuarioLogado.getAssinaturas()
        );

        // 1. Atualizar KPIs
        double totalGasto = 0;
        int ativasCont = 0;
        Date proximoVenc = null;
        String proximoNome = "";

        List<Assinatura> doUsuario = new ArrayList<>();
        for (Assinatura a : ctrlAssinatura.getTodasAssinaturas()) {
            if (a.getUsuarioId() == usuarioLogado.getId()) {
                doUsuario.add(a);
                if (a.getStatus() != null && a.getStatus().equalsIgnoreCase("Ativo")) {
                    totalGasto += a.getValor();
                    ativasCont++;
                    if (a.getDataVencimento() != null) {
                        if (proximoVenc == null || a.getDataVencimento().before(proximoVenc)) {
                            proximoVenc = a.getDataVencimento();
                            proximoNome = a.getNomeServico();
                        }
                    }
                }
            }
        }
        usuarioLogado.setAssinaturas(doUsuario);

        lblTotalGasto.setText("R$ " + String.format("%.2f", totalGasto));
        lblQtdAssinaturas.setText(String.valueOf(ativasCont));
        if (proximoVenc != null) {
            lblProximoVencimento.setText(proximoNome + " (" + dateFormat.format(proximoVenc) + ")");
        } else {
            lblProximoVencimento.setText("Nenhum");
        }

        // 2. Popular Tabela
        modeloTabela.setRowCount(0);
        for (Assinatura a : doUsuario) {
            modeloTabela.addRow(new Object[]{
                    a.getId(),
                    a.getNomeServico(),
                    "R$ " + String.format("%.2f", a.getValor()),
                    a.getDataVencimento() != null ? dateFormat.format(a.getDataVencimento()) : "N/A",
                    a.getCategoria() != null ? a.getCategoria().getNome() : "Sem Categoria",
                    a.getPeriodicidade().getDescricao(),
                    a.getStatus() != null ? a.getStatus() : "Ativo"
            });
        }

        // 3. Atualizar Dropdown de Categorias
        String selecionado = (String) cbFiltroCategoria.getSelectedItem();
        cbFiltroCategoria.removeAllItems();
        cbFiltroCategoria.addItem("Todas");
        for (Assinatura a : doUsuario) {
            if (a.getCategoria() != null) {
                String cat = a.getCategoria().getNome();
                boolean existe = false;
                for (int i = 0; i < cbFiltroCategoria.getItemCount(); i++) {
                    if (cbFiltroCategoria.getItemAt(i).equalsIgnoreCase(cat)) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    cbFiltroCategoria.addItem(cat);
                }
            }
        }
        if (selecionado != null) {
            boolean existe = false;
            for (int i = 0; i < cbFiltroCategoria.getItemCount(); i++) {
                if (cbFiltroCategoria.getItemAt(i).equalsIgnoreCase(selecionado)) {
                    existe = true;
                    break;
                }
            }
            if (existe) {
                cbFiltroCategoria.setSelectedItem(selecionado);
            } else {
                cbFiltroCategoria.setSelectedItem("Todas");
            }
        } else {
            cbFiltroCategoria.setSelectedItem("Todas");
        }
    }

    private void carregarAlertasIniciais() {
        // Gera notificações simuladas para alertas de vencimento rápido (RU-04)
        List<Usuario> list = new ArrayList<>();
        list.add(usuarioLogado);
        ctrlAlerta.executarVerificacaoDiaria(list, 10); // Notifica vencimentos nos próximos 10 dias
        atualizarExibicaoAlertas();
    }

    private void atualizarExibicaoAlertas() {
        panelNotificacoes.removeAll();
        alertasAtivos.clear();

        for (Alerta alerta : usuarioLogado.getAlertas()) {
            alertasAtivos.add(alerta);
        }

        if (alertasAtivos.isEmpty()) {
            panelNotificacoes.setVisible(false);
        } else {
            panelNotificacoes.setVisible(true);
            
            JLabel lblNotifTitle = new JLabel("Alertas e Lembretes Importantes:");
            lblNotifTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblNotifTitle.setForeground(COLOR_ACCENT_RED);
            lblNotifTitle.setBorder(new EmptyBorder(5, 5, 5, 0));
            panelNotificacoes.add(lblNotifTitle);

            for (Alerta a : alertasAtivos) {
                JPanel alertaCard = new JPanel(new BorderLayout());
                alertaCard.setBackground(new Color(45, 27, 27)); // Vermelho bem escuro
                alertaCard.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_ACCENT_RED, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                alertaCard.setMaximumSize(new Dimension(800, 45));
                alertaCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel msg = new JLabel(a.getMensagem());
                msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                msg.setForeground(COLOR_TEXT_PRIMARY);

                JLabel close = new JLabel("✕");
                close.setFont(new Font("Segoe UI", Font.BOLD, 12));
                close.setForeground(COLOR_TEXT_MUTED);
                close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                close.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        usuarioLogado.getAlertas().remove(a);
                        atualizarExibicaoAlertas();
                    }
                });

                alertaCard.add(msg, BorderLayout.CENTER);
                alertaCard.add(close, BorderLayout.EAST);

                panelNotificacoes.add(alertaCard);
                panelNotificacoes.add(Box.createVerticalStrut(5));
            }
        }

        panelNotificacoes.revalidate();
        panelNotificacoes.repaint();
    }

    private void filtrarAssinaturasTabela() {
        String catSelecionada = (String) cbFiltroCategoria.getSelectedItem();
        if (catSelecionada == null) return;

        modeloTabela.setRowCount(0);
        for (Assinatura a : usuarioLogado.getAssinaturas()) {
            String c = (a.getCategoria() != null) ? a.getCategoria().getNome() : "Sem Categoria";
            if (catSelecionada.equals("Todas") || c.equalsIgnoreCase(catSelecionada)) {
                modeloTabela.addRow(new Object[]{
                        a.getId(),
                        a.getNomeServico(),
                        "R$ " + String.format("%.2f", a.getValor()),
                        a.getDataVencimento() != null ? dateFormat.format(a.getDataVencimento()) : "N/A",
                        c,
                        a.getPeriodicidade().getDescricao(),
                        a.getStatus() != null ? a.getStatus() : "Ativo"
                });
            }
        }
    }

    private void abrirTelaCadastroAssinatura() {
        TelaAssinatura form = new TelaAssinatura(this, usuarioLogado, ctrlAssinatura);
        form.setVisible(true);

        if (form.isSalvoComSucesso()) {
            atualizarDadosPainel();
        }
    }

    private void abrirTelaEdicaoAssinatura() {
        int selectedRow = tabelaAssinaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma assinatura da tabela para editar.", "Editar Assinatura", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int subId = (Integer) modeloTabela.getValueAt(selectedRow, 0);
        Assinatura sub = ctrlAssinatura.selecionarAssinatura(subId, usuarioLogado.getAssinaturas());

        if (sub != null) {
            TelaAssinatura form = new TelaAssinatura(this, usuarioLogado, ctrlAssinatura, sub);
            form.setVisible(true);

            if (form.isSalvoComSucesso()) {
                atualizarDadosPainel();
            }
        }
    }

    private void processarExcluirAssinatura() {
        int selectedRow = tabelaAssinaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma assinatura para excluir.", "Excluir Assinatura", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int subId = (Integer) modeloTabela.getValueAt(selectedRow, 0);
        String nome = (String) modeloTabela.getValueAt(selectedRow, 1);

        UIManager.put("OptionPane.background", COLOR_CARD);
        UIManager.put("Panel.background", COLOR_CARD);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", COLOR_PRIMARY);
        UIManager.put("Button.foreground", Color.WHITE);

        int op = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir permanentemente a assinatura de '" + nome + "'?", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (op == JOptionPane.YES_OPTION) {
            boolean sucesso = ctrlAssinatura.excluirAssinatura(subId, usuarioLogado.getAssinaturas());
            if (sucesso) {
                atualizarDadosPainel();
                JOptionPane.showMessageDialog(this, "Assinatura removida com sucesso.", "Confirmação", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir a assinatura.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processarRenovacaoManual() {
        int selectedRow = tabelaAssinaturas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma assinatura para confirmar a renovação e avançar a data de vencimento.", "Renovação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int subId = (Integer) modeloTabela.getValueAt(selectedRow, 0);
        Assinatura sub = ctrlAssinatura.selecionarAssinatura(subId, usuarioLogado.getAssinaturas());

        if (sub != null) {
            sub.renovarAssinatura(); // Executa renovação calculada
            // Salva as alterações
            ctrlAssinatura.editarDados(sub.getId(), sub, usuarioLogado.getAssinaturas());
            atualizarDadosPainel();
            JOptionPane.showMessageDialog(this, "Data de vencimento estendida com sucesso com base no período recorrente!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simularVerificacaoAlertas() {
        carregarAlertasIniciais();
        JOptionPane.showMessageDialog(this, 
                "Verificação de alertas executada!\nNovas notificações foram exibidas no painel de avisos caso haja vencimentos próximos (10 dias).", 
                "Sistema de Alertas", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirPainelRelatorios() {
        RelatorioUI dialog = new RelatorioUI(this, usuarioLogado, ctrlRelatorio);
        dialog.setVisible(true);
    }

    private void processarLogout() {
        ctrlUsuario.logout();
        SwingUtilities.invokeLater(() -> {
            TelaLogin login = new TelaLogin(ctrlUsuario, ctrlAssinatura, ctrlRelatorio, ctrlAlerta);
            login.setVisible(true);
            this.dispose();
        });
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

package view;

import controller.ControladorAssinatura;
import model.Assinatura;
import model.Categoria;
import model.Periodicidade;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Formulário Modal para Cadastrar ou Editar Assinaturas (Dark Mode Premium).
 */
public class TelaAssinatura extends JDialog {

    private ControladorAssinatura ctrlAssinatura;
    private Usuario usuarioLogado;
    private Assinatura assinaturaEdicao; // Null se for modo cadastro
    private boolean salvoComSucesso = false;

    // Componentes de UI
    private JTextField txtServico;
    private JTextField txtValor;
    private JTextField txtVencimento;
    private JComboBox<String> cbCategoria;
    private JComboBox<Periodicidade> cbPeriodicidade;
    private JComboBox<String> cbStatus;
    private JButton btnSalvar;
    private JButton btnCancelar;

    // Cores Premium
    private static final Color COLOR_BG = new Color(18, 18, 20); // Fundo escuro profundo
    private static final Color COLOR_CARD = new Color(29, 29, 34); // Card cinza escuro
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color COLOR_PRIMARY_HOVER = new Color(79, 70, 229);
    private static final Color COLOR_FIELD_BG = new Color(42, 42, 48); // Fundo dos campos
    private static final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private static final Color COLOR_TEXT_MUTED = new Color(156, 163, 175);
    private static final Color COLOR_BORDER = new Color(63, 63, 70);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Construtor para MODO CADASTRO.
     */
    public TelaAssinatura(Frame parent, Usuario usuarioLogado, ControladorAssinatura ctrlAssinatura) {
        super(parent, "Cadastrar Assinatura", true);
        this.usuarioLogado = usuarioLogado;
        this.ctrlAssinatura = ctrlAssinatura;
        this.assinaturaEdicao = null;

        configurarDialog();
        inicializarComponentes();
        preencherDadosPadrao();
        setLocationRelativeTo(parent);
    }

    /**
     * Construtor para MODO EDIÇÃO.
     */
    public TelaAssinatura(Frame parent, Usuario usuarioLogado, ControladorAssinatura ctrlAssinatura, Assinatura assinatura) {
        super(parent, "Editar Assinatura", true);
        this.usuarioLogado = usuarioLogado;
        this.ctrlAssinatura = ctrlAssinatura;
        this.assinaturaEdicao = assinatura;

        configurarDialog();
        inicializarComponentes();
        carregarDadosEdicao();
        setLocationRelativeTo(parent);
    }

    private void configurarDialog() {
        setSize(450, 580);
        setResizable(true);
        setMinimumSize(new Dimension(450, 580));
        getContentPane().setBackground(COLOR_BG);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        // Card de Conteúdo
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setPreferredSize(new Dimension(390, 510));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        // Título da Tela
        JLabel lblTitulo = new JLabel(assinaturaEdicao == null ? "Nova Assinatura" : "Editar Assinatura");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Inputs
        JPanel panelServico = criarCampoTexto("Nome do Serviço (ex: Netflix, Spotify)", txtServico = new JTextField());
        JPanel panelValor = criarCampoTexto("Valor Mensal/Anual (ex: 29.90)", txtValor = new JTextField());
        JPanel panelVencimento = criarCampoTexto("Próximo Vencimento (dd/mm/aaaa)", txtVencimento = new JTextField());

        // ComboBox Categoria
        cbCategoria = new JComboBox<>() {
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
        cbCategoria.setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCategoria.addActionListener(e -> verificarNovaCategoria());
        JPanel panelCategoria = criarCampoContainer("Categoria", cbCategoria);

        // ComboBox Periodicidade
        cbPeriodicidade = new JComboBox<>(Periodicidade.values()) {
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
        cbPeriodicidade.setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
        cbPeriodicidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JPanel panelPeriodicidade = criarCampoContainer("Periodicidade", cbPeriodicidade);

        // ComboBox Status
        cbStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"}) {
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
        cbStatus.setRenderer(new DarkComboBoxRenderer(COLOR_FIELD_BG, COLOR_TEXT_PRIMARY, COLOR_PRIMARY));
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JPanel panelStatus = criarCampoContainer("Status do Serviço", cbStatus);

        // Botões (Salvar & Cancelar)
        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotoes.setBackground(COLOR_CARD);
        panelBotoes.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        configurarBotao(btnCancelar, new Color(63, 63, 70), COLOR_TEXT_PRIMARY);
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton("Confirmar");
        configurarBotao(btnSalvar, COLOR_PRIMARY, COLOR_TEXT_PRIMARY);
        btnSalvar.addActionListener(e -> processarSalvarComRetentativa());

        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);

        // Adicionar ao Card
        cardPanel.add(lblTitulo);
        cardPanel.add(panelServico);
        cardPanel.add(panelValor);
        cardPanel.add(panelVencimento);
        cardPanel.add(panelCategoria);
        cardPanel.add(panelPeriodicidade);
        cardPanel.add(panelStatus);
        cardPanel.add(panelBotoes);

        add(cardPanel);
    }

    private JPanel criarCampoTexto(String labelTexto, JTextField campo) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_CARD);
        container.setBorder(new EmptyBorder(0, 0, 10, 0));
        container.setMaximumSize(new Dimension(340, 58));
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelTexto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(COLOR_TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 3, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setBackground(COLOR_FIELD_BG);
        campo.setForeground(COLOR_TEXT_PRIMARY);
        campo.setCaretColor(COLOR_TEXT_PRIMARY);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        campo.setMaximumSize(new Dimension(340, 32));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(label);
        container.add(campo);
        return container;
    }

    private JPanel criarCampoContainer(String labelTexto, JComponent combo) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_CARD);
        container.setBorder(new EmptyBorder(0, 0, 10, 0));
        container.setMaximumSize(new Dimension(340, 58));
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelTexto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(COLOR_TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 3, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        combo.setMaximumSize(new Dimension(340, 32));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(label);
        container.add(combo);
        return container;
    }

    private void configurarBotao(JButton btn, Color corFundo, Color corTexto) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(corFundo);
        btn.setForeground(corTexto);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (corFundo.equals(COLOR_PRIMARY)) {
                    btn.setBackground(COLOR_PRIMARY_HOVER);
                } else {
                    btn.setBackground(corFundo.brighter());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(corFundo);
            }
        });
    }

    private void preencherDadosPadrao() {
        // Carrega categorias conhecidas das assinaturas atuais
        recarregarCategorias(null);
        cbPeriodicidade.setSelectedItem(Periodicidade.MENSAL);
        cbStatus.setSelectedItem("Ativo");
        txtVencimento.setText(dateFormat.format(new Date()));
    }

    private void carregarDadosEdicao() {
        if (assinaturaEdicao == null) return;

        txtServico.setText(assinaturaEdicao.getNomeServico());
        txtValor.setText(String.valueOf(assinaturaEdicao.getValor()));
        if (assinaturaEdicao.getDataVencimento() != null) {
            txtVencimento.setText(dateFormat.format(assinaturaEdicao.getDataVencimento()));
        }
        
        String catNome = (assinaturaEdicao.getCategoria() != null) ? assinaturaEdicao.getCategoria().getNome() : "Streaming";
        recarregarCategorias(catNome);
        
        cbPeriodicidade.setSelectedItem(assinaturaEdicao.getPeriodicidade());
        cbStatus.setSelectedItem(assinaturaEdicao.getStatus() != null ? assinaturaEdicao.getStatus() : "Ativo");
    }

    private void recarregarCategorias(String selecionar) {
        cbCategoria.removeAllItems();
        
        // Categorias base sempre presentes
        cbCategoria.addItem("Streaming");
        cbCategoria.addItem("Trabalho");
        cbCategoria.addItem("Games");
        cbCategoria.addItem("Educação");
        cbCategoria.addItem("Saúde");

        // Busca categorias personalizadas já existentes
        List<Assinatura> list = ctrlAssinatura.getTodasAssinaturas();
        for (Assinatura a : list) {
            if (a.getCategoria() != null) {
                String nomeCat = a.getCategoria().getNome();
                boolean existe = false;
                for (int i = 0; i < cbCategoria.getItemCount(); i++) {
                    if (cbCategoria.getItemAt(i).equalsIgnoreCase(nomeCat)) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    cbCategoria.addItem(nomeCat);
                }
            }
        }

        // Opção para adicionar nova categoria
        cbCategoria.addItem("+ Nova Categoria...");

        if (selecionar != null) {
            cbCategoria.setSelectedItem(selecionar);
        }
    }

    private void verificarNovaCategoria() {
        String selecionado = (String) cbCategoria.getSelectedItem();
        if (selecionado != null && selecionado.equals("+ Nova Categoria...")) {
            UIManager.put("OptionPane.background", COLOR_CARD);
            UIManager.put("Panel.background", COLOR_CARD);
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Button.background", COLOR_PRIMARY);
            UIManager.put("Button.foreground", Color.WHITE);

            String novaCat = JOptionPane.showInputDialog(this, 
                    "Digite o nome da nova categoria:", 
                    "Nova Categoria", 
                    JOptionPane.QUESTION_MESSAGE);

            if (novaCat != null && !novaCat.trim().isEmpty()) {
                novaCat = novaCat.trim();
                // Remove o item "+ Nova Categoria..." temporariamente, insere a nova e recoloca
                cbCategoria.removeItem("+ Nova Categoria...");
                cbCategoria.addItem(novaCat);
                cbCategoria.addItem("+ Nova Categoria...");
                cbCategoria.setSelectedItem(novaCat);
            } else {
                cbCategoria.setSelectedIndex(0); // Volta ao padrão
            }
        }
    }

    private void processarSalvarComRetentativa() {
        // Implementa o Loop de Retentativa do Diagrama de Atividades
        String nome = txtServico.getText().trim();
        String valorStr = txtValor.getText().trim();
        String vencStr = txtVencimento.getText().trim();
        String catNome = (String) cbCategoria.getSelectedItem();
        Periodicidade per = (Periodicidade) cbPeriodicidade.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        // Limpa a string da categoria se for o seletor especial
        if (catNome != null && catNome.equals("+ Nova Categoria...")) {
            catNome = "Geral";
        }

        // 1. Validar e analisar dados inseridos
        double valor = 0;
        Date vencimento = null;
        boolean dadosValidos = true;
        StringBuilder erros = new StringBuilder();

        if (nome.isEmpty()) {
            dadosValidos = false;
            erros.append("- O nome do serviço é obrigatório.\n");
        }

        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                dadosValidos = false;
                erros.append("- O valor deve ser maior que zero.\n");
            }
        } catch (NumberFormatException ex) {
            dadosValidos = false;
            erros.append("- O valor informado é inválido (ex: 29.90).\n");
        }

        try {
            vencimento = dateFormat.parse(vencStr);
        } catch (Exception ex) {
            dadosValidos = false;
            erros.append("- A data de vencimento deve estar no formato dd/MM/aaaa.\n");
        }

        // Decisão do Diagrama: Dados Válidos?
        if (!dadosValidos) {
            // Não -> Exibe mensagem de erro e retorna para correção (mantendo formulário aberto)
            UIManager.put("OptionPane.background", COLOR_CARD);
            UIManager.put("Panel.background", COLOR_CARD);
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Button.background", COLOR_PRIMARY);
            UIManager.put("Button.foreground", Color.WHITE);

            JOptionPane.showMessageDialog(this, 
                    "Dados inválidos! Por favor, corrija os erros abaixo:\n\n" + erros.toString(), 
                    "Erro de Validação", 
                    JOptionPane.ERROR_MESSAGE);
            return; // Loop: Retorna para correção
        }

        // Sim -> Salvar no Banco
        Categoria categoria = new Categoria(0, catNome, "Categoria da assinatura");
        
        if (assinaturaEdicao == null) {
            // MODO CADASTRO
            int novoId = 1;
            for (Assinatura a : ctrlAssinatura.getTodasAssinaturas()) {
                if (a.getId() >= novoId) {
                    novoId = a.getId() + 1;
                }
            }
            Assinatura novaSub = new Assinatura(novoId, nome, valor, vencimento, status, categoria, per, usuarioLogado.getId());
            
            boolean sucesso = ctrlAssinatura.cadastrarAssinatura(novaSub);
            if (sucesso) {
                salvoComSucesso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar assinatura no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // MODO EDIÇÃO
            Assinatura novosDados = new Assinatura(assinaturaEdicao.getId(), nome, valor, vencimento, status, categoria, per, usuarioLogado.getId());
            boolean sucesso = ctrlAssinatura.editarDados(assinaturaEdicao.getId(), novosDados, usuarioLogado.getAssinaturas());
            if (sucesso) {
                salvoComSucesso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar dados da assinatura.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
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

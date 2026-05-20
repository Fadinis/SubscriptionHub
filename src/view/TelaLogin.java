package view;

import controller.ControladorUsuario;
import controller.ControladorAssinatura;
import controller.ControladorRelatorio;
import controller.ControladorAlerta;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Interface Gráfica de Login e Cadastro de Usuários (Dark Mode Premium).
 */
public class TelaLogin extends JFrame {

    private ControladorUsuario ctrlUsuario;
    private ControladorAssinatura ctrlAssinatura;
    private ControladorRelatorio ctrlRelatorio;
    private ControladorAlerta ctrlAlerta;

    // Componentes de UI
    private JPanel cardPanel;
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnAcao;
    private JLabel lblAlternar;
    private JPanel panelNome;

    // Cores Premium
    private static final Color COLOR_BG = new Color(18, 18, 20); // Fundo escuro profundo
    private static final Color COLOR_CARD = new Color(29, 29, 34); // Card cinza escuro
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color COLOR_PRIMARY_HOVER = new Color(79, 70, 229);
    private static final Color COLOR_FIELD_BG = new Color(42, 42, 48); // Fundo dos campos
    private static final Color COLOR_TEXT_PRIMARY = Color.WHITE;
    private static final Color COLOR_TEXT_MUTED = new Color(156, 163, 175); // Cinza claro mutado
    private static final Color COLOR_ACCENT = new Color(16, 185, 129); // Verde Esmeralda

    private boolean modoRegistro = false;

    public TelaLogin(ControladorUsuario ctrlUsuario, ControladorAssinatura ctrlAssinatura, 
                     ControladorRelatorio ctrlRelatorio, ControladorAlerta ctrlAlerta) {
        this.ctrlUsuario = ctrlUsuario;
        this.ctrlAssinatura = ctrlAssinatura;
        this.ctrlRelatorio = ctrlRelatorio;
        this.ctrlAlerta = ctrlAlerta;

        configurarJanela();
        inicializarComponentes();
        setLocationRelativeTo(null);
    }

    private void configurarJanela() {
        setTitle("Subscription Hub - Acesso");
        setSize(450, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(450, 580));
        getContentPane().setBackground(COLOR_BG);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        // Card Centralizado
        cardPanel = new JPanel();
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setPreferredSize(new Dimension(380, 500));
        cardPanel.setMinimumSize(new Dimension(380, 500));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(63, 63, 70), 1, true),
                new EmptyBorder(30, 30, 30, 30)
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        // Título "Subscription Hub"
        lblTitulo = new JLabel("Subscription Hub");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo descritivo
        lblSubtitulo = new JLabel("Gerencie suas assinaturas em um só lugar");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(COLOR_TEXT_MUTED);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(new EmptyBorder(5, 0, 30, 0));

        // Campo de Nome (Oculto por padrão no modo login)
        panelNome = criarCampoTexto("Nome Completo", txtNome = new JTextField());
        panelNome.setVisible(false);

        // Campo de E-mail
        JPanel panelEmail = criarCampoTexto("Endereço de E-mail", txtEmail = new JTextField());

        // Campo de Senha
        JPanel panelSenha = criarCampoTexto("Sua Senha", txtSenha = new JPasswordField());
        panelSenha.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Botão Principal de Ação (Login)
        btnAcao = new JButton("Entrar na Plataforma");
        btnAcao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAcao.setForeground(COLOR_TEXT_PRIMARY);
        btnAcao.setBackground(COLOR_PRIMARY);
        btnAcao.setFocusPainted(false);
        btnAcao.setBorderPainted(false);
        btnAcao.setOpaque(true);
        btnAcao.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAcao.setMaximumSize(new Dimension(320, 45));
        btnAcao.setPreferredSize(new Dimension(320, 45));
        btnAcao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito Hover no Botão
        btnAcao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAcao.setBackground(COLOR_PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnAcao.setBackground(COLOR_PRIMARY);
            }
        });

        btnAcao.addActionListener(e -> processarAcaoPrincipal());

        // Link de Alternância entre Login/Cadastro
        lblAlternar = new JLabel("Ainda não tem conta? Crie uma aqui");
        lblAlternar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlternar.setForeground(COLOR_PRIMARY);
        lblAlternar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAlternar.setBorder(new EmptyBorder(25, 0, 0, 0));
        lblAlternar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblAlternar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternarModo();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblAlternar.setForeground(COLOR_PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblAlternar.setForeground(COLOR_PRIMARY);
            }
        });

        // Montagem do Card
        cardPanel.add(lblTitulo);
        cardPanel.add(lblSubtitulo);
        cardPanel.add(panelNome);
        cardPanel.add(panelEmail);
        cardPanel.add(panelSenha);
        cardPanel.add(btnAcao);
        cardPanel.add(lblAlternar);

        add(cardPanel);
    }

    private JPanel criarCampoTexto(String labelTexto, JTextField campo) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_CARD);
        container.setBorder(new EmptyBorder(0, 0, 15, 0));
        container.setMaximumSize(new Dimension(320, 70));
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelTexto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setBackground(COLOR_FIELD_BG);
        campo.setForeground(COLOR_TEXT_PRIMARY);
        campo.setCaretColor(COLOR_TEXT_PRIMARY);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(63, 63, 70), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        campo.setMaximumSize(new Dimension(320, 38));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Feedback visual ao focar no campo
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_PRIMARY, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(63, 63, 70), 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        container.add(label);
        container.add(campo);
        return container;
    }

    private void alternarModo() {
        modoRegistro = !modoRegistro;
        limparCampos();

        if (modoRegistro) {
            panelNome.setVisible(true);
            lblSubtitulo.setText("Preencha os dados abaixo para se registrar");
            btnAcao.setText("Criar Nova Conta");
            lblAlternar.setText("Já possui uma conta? Faça login aqui");
            setMinimumSize(new Dimension(450, 620));
            if (getWidth() < 450 || getHeight() < 620) {
                setSize(Math.max(getWidth(), 450), Math.max(getHeight(), 620));
            }
        } else {
            panelNome.setVisible(false);
            lblSubtitulo.setText("Gerencie suas assinaturas em um só lugar");
            btnAcao.setText("Entrar na Plataforma");
            lblAlternar.setText("Ainda não tem conta? Crie uma aqui");
            setMinimumSize(new Dimension(450, 580));
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private void processarAcaoPrincipal() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarMensagemErro("Preencha todos os campos obrigatórios.");
            return;
        }

        if (modoRegistro) {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                mostrarMensagemErro("Por favor, preencha seu nome.");
                return;
            }

            Usuario novo = ctrlUsuario.registrarUsuario(nome, email, senha);
            if (novo != null) {
                JOptionPane.showMessageDialog(this, 
                        "Conta criada com sucesso! Você já pode efetuar seu login.", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                alternarModo();
            } else {
                mostrarMensagemErro("Falha ao registrar usuário. Verifique se o e-mail já está em uso.");
            }
        } else {
            Usuario autenticado = ctrlUsuario.autenticar(email, senha);
            if (autenticado != null) {
                abrirPainelDashboard(autenticado);
            } else {
                mostrarMensagemErro("E-mail ou senha incorretos. Tente novamente.");
            }
        }
    }

    private void abrirPainelDashboard(Usuario usuario) {
        System.out.println("[TELA LOGIN] Redirecionando " + usuario.getNome() + " para o Dashboard...");
        
        // Garante que o usuário possua suas assinaturas sincronizadas com o banco em memória
        usuario.setAssinaturas(ctrlAssinatura.consultarAssinaturas(usuario.getId(), ctrlAssinatura.getTodasAssinaturas()));

        // Inicializa o PainelUI
        SwingUtilities.invokeLater(() -> {
            PainelUI dashboard = new PainelUI(usuario, ctrlUsuario, ctrlAssinatura, ctrlRelatorio, ctrlAlerta);
            dashboard.setVisible(true);
            this.dispose(); // Fecha a tela de login
        });
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtSenha.setText("");
    }

    private void mostrarMensagemErro(String mensagem) {
        UIManager.put("OptionPane.background", COLOR_CARD);
        UIManager.put("Panel.background", COLOR_CARD);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", COLOR_PRIMARY);
        UIManager.put("Button.foreground", Color.WHITE);

        JOptionPane.showMessageDialog(this,
                mensagem,
                "Aviso de Validação",
                JOptionPane.WARNING_MESSAGE);
    }
}

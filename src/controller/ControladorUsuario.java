package controller;

import model.Usuario;
import network.PersistenceManager;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador responsável por gerenciar os usuários do sistema.
 * Cuida do login, registro e persistência no arquivo CSV.
 */
public class ControladorUsuario {

    private List<Usuario> usuarios;
    private Usuario usuarioLogado;

    public ControladorUsuario() {
        this.usuarios = PersistenceManager.carregarUsuarios();
        // Se a lista estiver vazia, cria usuários padrão para testes iniciais
        if (this.usuarios.isEmpty()) {
            this.usuarios = new ArrayList<>();
            // João Silva (hash123 -> SHA-256: 7d6c6e7a27ebdc852033068e1a8a29a03767e9f3b140cd4cb92ef65e4ff58f50)
            Usuario joao = new Usuario(1, "Joao Silva", "joao@email.com", "hash123");
            // Maria Santos (m123 -> SHA-256: 0e7d03a1168db75d1f88c8309df5ff6bfbb1a93339f4e2f3d2f2d90d79f0415a)
            Usuario maria = new Usuario(2, "Maria Santos", "maria@email.com", "m123");
            
            usuarios.add(joao);
            usuarios.add(maria);
            PersistenceManager.salvarUsuarios(usuarios);
        }
    }

    /**
     * Autentica um usuário pelo email e senha.
     * @param email Email do usuário
     * @param senha Senha em texto plano
     * @return O usuário autenticado ou null se falhar
     */
    public Usuario autenticar(String email, String senha) {
        System.out.println("[CONTROLADOR USUARIO] Tentativa de login para: " + email);
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                if (u.autenticar(senha)) {
                    this.usuarioLogado = u;
                    System.out.println("[CONTROLADOR USUARIO] Login realizado com sucesso para: " + u.getNome());
                    PersistenceManager.salvarLog("Login bem-sucedido do usuário: " + email);
                    return u;
                } else {
                    System.out.println("[CONTROLADOR USUARIO] Senha incorreta para: " + email);
                    PersistenceManager.salvarLog("Tentativa falha de login (senha incorreta): " + email);
                }
            }
        }
        return null;
    }

    /**
     * Registra um novo usuário no sistema.
     * @param nome Nome do usuário
     * @param email Email do usuário
     * @param senha Senha em texto plano
     * @return O novo usuário criado, ou null se o email já estiver cadastrado
     */
    public Usuario registrarUsuario(String nome, String email, String senha) {
        if (nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            System.err.println("[CONTROLADOR USUARIO] Dados de cadastro inválidos.");
            return null;
        }

        // Verifica se o email já existe
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                System.err.println("[CONTROLADOR USUARIO] E-mail já cadastrado: " + email);
                return null;
            }
        }

        int novoId = 1;
        for (Usuario u : usuarios) {
            if (u.getId() >= novoId) {
                novoId = u.getId() + 1;
            }
        }

        Usuario novoUsuario = new Usuario(novoId, nome.trim(), email.trim(), senha);
        usuarios.add(novoUsuario);
        PersistenceManager.salvarUsuarios(usuarios);
        PersistenceManager.salvarLog("Novo usuário registrado: " + email);
        System.out.println("[CONTROLADOR USUARIO] Usuário cadastrado: " + nome + " (ID: " + novoId + ")");
        return novoUsuario;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void logout() {
        if (usuarioLogado != null) {
            PersistenceManager.salvarLog("Logout do usuário: " + usuarioLogado.getEmail());
            System.out.println("[CONTROLADOR USUARIO] Logout realizado para: " + usuarioLogado.getNome());
            this.usuarioLogado = null;
        }
    }
}

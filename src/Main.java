import model.*;
import controller.*;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   SUBSCRIPTION HUB - INICIANDO SISTEMA GUI      ");
        System.out.println("=================================================\n");

        // 1. SETUP DE CONTROLADORES E REDE
        ControladorUsuario ctrlUsuario = new ControladorUsuario();
        ControladorAssinatura ctrlAssinatura = new ControladorAssinatura();
        ControladorRelatorio ctrlRelatorio = new ControladorRelatorio();
        ControladorAlerta ctrlAlerta = new ControladorAlerta();
        
        // Iniciar camada de rede para múltiplos usuários (RS-02)
        network.ServidorSocket servidor = new network.ServidorSocket(ctrlAssinatura);
        servidor.iniciarServidor();

        // 2. LAUNCH DA INTERFACE GRÁFICA (FRONT-END)
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look & Feel nativo do sistema para melhor integração visual
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.out.println("[GUI] Falha ao definir look & feel: " + e.getMessage());
            }

            System.out.println("[GUI] Abrindo Tela de Acesso (Login)...");
            view.TelaLogin login = new view.TelaLogin(ctrlUsuario, ctrlAssinatura, ctrlRelatorio, ctrlAlerta);
            login.setVisible(true);
        });
    }
}
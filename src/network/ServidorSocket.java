package network;

import controller.ControladorAssinatura;
import controller.ControladorAlerta;
import model.Usuario;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Requisito RS-02: O sistema deve permitir multiplos usuarios simultaneos.
 * Implementacao da camada de Network para centralizacao de dados.
 */
public class ServidorSocket {
    private static final int PORTA = 12345;
    private ControladorAssinatura ctrlAssinatura;
    private boolean executando = true;

    public ServidorSocket(ControladorAssinatura ctrlAssinatura) {
        this.ctrlAssinatura = ctrlAssinatura;
    }

    public void iniciarServidor() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
                System.out.println("[NETWORK] Servidor centralizado iniciado na porta " + PORTA);
                
                while (executando) {
                    Socket clienteSocket = serverSocket.accept();
                    // RS-02: Tratamento de multiplos usuarios via Threads
                    new Thread(new ManipuladorCliente(clienteSocket, ctrlAssinatura)).start();
                }
            } catch (IOException e) {
                System.err.println("[NETWORK] Erro no servidor: " + e.getMessage());
            }
        }).start();
    }

    public void pararServidor() {
        this.executando = false;
    }

    /**
     * Classe interna para manipular cada conexao de usuario simultaneamente.
     */
    private static class ManipuladorCliente implements Runnable {
        private Socket socket;
        private ControladorAssinatura ctrlAssinatura;

        public ManipuladorCliente(Socket socket, ControladorAssinatura ctrlAssinatura) {
            this.socket = socket;
            this.ctrlAssinatura = ctrlAssinatura;
        }

        @Override
        public void run() {
            String ipCliente = socket.getInetAddress().getHostAddress();
            System.out.println("[NETWORK] Nova conexao estabelecida: " + ipCliente);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("BEM-VINDO AO SUBSCRIPTION HUB CENTRAL");
                
                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equalsIgnoreCase("SAIR")) break;
                    
                    // Exemplo de comando da API
                    if (request.startsWith("LISTAR")) {
                        out.println("LOG: Solicitando lista via IP " + ipCliente);
                        // Aqui o servidor usaria o controlador para retornar dados
                        out.println("STATUS: OK | DADOS: [Simulacao de transmissao de dados]");
                    } else {
                        out.println("COMANDO NAO RECONHECIDO");
                    }
                }
            } catch (IOException e) {
                System.err.println("[NETWORK] Erro na conexao com " + ipCliente + ": " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("[NETWORK] Conexao encerrada: " + ipCliente);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

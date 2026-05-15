package network;

import model.Assinatura;
import model.Usuario;
import model.Categoria;
import model.Periodicidade;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Gerencia a persistencia de dados em arquivos locais.
 * Como nao temos bibliotecas JSON externas (GSON/Jackson), 
 * utilizaremos um formato CSV simples para assinaturas e usuarios.
 */
public class PersistenceManager {

    private static final String FILE_ASSINATURAS = "assinaturas.csv";
    private static final String FILE_USUARIOS = "usuarios.csv";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Salva a lista de assinaturas em um arquivo CSV.
     */
    public synchronized static void salvarAssinaturas(List<Assinatura> assinaturas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_ASSINATURAS))) {
            for (Assinatura a : assinaturas) {
                String data = (a.getDataVencimento() != null) ? dateFormat.format(a.getDataVencimento()) : "";
                String cat = (a.getCategoria() != null) ? a.getCategoria().getNome() : "Sem Categoria";
                
                writer.println(a.getId() + ";" + 
                               a.getNomeServico() + ";" + 
                               a.getValor() + ";" + 
                               data + ";" + 
                               a.getStatus() + ";" + 
                               cat + ";" + 
                               a.getPeriodicidade().name() + ";" +
                               a.getUsuarioId());
            }
            System.out.println("[SISTEMA] Dados de assinaturas salvos localmente.");
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao salvar assinaturas: " + e.getMessage());
        }
    }

    /**
     * Carrega as assinaturas do arquivo CSV.
     */
    public synchronized static List<Assinatura> carregarAssinaturas() {
        List<Assinatura> assinaturas = new ArrayList<>();
        File file = new File(FILE_ASSINATURAS);
        if (!file.exists()) return assinaturas;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 8) {
                    int id = Integer.parseInt(parts[0]);
                    String nome = parts[1];
                    double valor = Double.parseDouble(parts[2]);
                    Date data = parts[3].isEmpty() ? null : dateFormat.parse(parts[3]);
                    String status = parts[4];
                    Categoria cat = new Categoria(0, parts[5], "");
                    Periodicidade per = Periodicidade.valueOf(parts[6]);
                    int uId = Integer.parseInt(parts[7]);
                    
                    assinaturas.add(new Assinatura(id, nome, valor, data, status, cat, per, uId));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao carregar assinaturas: " + e.getMessage());
        }
        return assinaturas;
    }

    /**
     * Salva a lista de usuarios em um arquivo CSV.
     */
    public synchronized static void salvarUsuarios(List<Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_USUARIOS))) {
            for (Usuario u : usuarios) {
                writer.println(u.getId() + ";" + u.getNome() + ";" + u.getEmail() + ";" + u.getSenhaHash());
            }
            System.out.println("[SISTEMA] Dados de usuarios salvos localmente.");
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao salvar usuarios: " + e.getMessage());
        }
    }

    /**
     * Carrega os usuarios do arquivo CSV.
     */
    public synchronized static List<Usuario> carregarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        File file = new File(FILE_USUARIOS);
        if (!file.exists()) return usuarios;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    String nome = parts[1];
                    String email = parts[2];
                    String hash = parts[3];
                    
                    Usuario u = new Usuario();
                    u.setId(id);
                    u.setNome(nome);
                    u.setEmail(email);
                    u.setSenhaHash(hash);
                    usuarios.add(u);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao carregar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    private static final String FILE_LOGS = "logs_sistema.txt";

    public synchronized static void salvarLog(String log) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_LOGS, true)))) {
            out.println("[" + dateFormat.format(new Date()) + "] " + log);
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao registrar log: " + e.getMessage());
        }
    }

    /**
     * Requisito RU-10: Exportar relatorio para arquivo.
     */
    public static void exportarRelatorioArquivo(String conteudo, String formato) {
        String nomeArquivo = "relatorio_exportado." + formato.toLowerCase();
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println(conteudo);
            System.out.println("[SISTEMA] Relatorio exportado com sucesso para: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao exportar relatorio: " + e.getMessage());
        }
    }
}

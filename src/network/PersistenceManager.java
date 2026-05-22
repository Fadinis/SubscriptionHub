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
        if (formato.equalsIgnoreCase("PDF")) {
            byte[] pdfBytes = gerarPdfValido(conteudo);
            try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
                fos.write(pdfBytes);
                System.out.println("[SISTEMA] Relatorio exportado com sucesso para (PDF binario): " + nomeArquivo);
            } catch (IOException e) {
                System.err.println("[ERRO] Falha ao exportar relatorio em PDF: " + e.getMessage());
            }
        } else {
            try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
                // Adiciona o BOM do UTF-8 para que o Excel reconheça a codificação corretamente
                fos.write(0xEF);
                fos.write(0xBB);
                fos.write(0xBF);
                
                try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos, java.nio.charset.StandardCharsets.UTF_8))) {
                    writer.println(conteudo);
                }
                System.out.println("[SISTEMA] Relatorio exportado com sucesso para: " + nomeArquivo);
            } catch (IOException e) {
                System.err.println("[ERRO] Falha ao exportar relatorio: " + e.getMessage());
            }
        }
    }

    private static String escapePdfString(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(' || c == ')' || c == '\\') {
                sb.append('\\');
                sb.append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static byte[] gerarPdfValido(String conteudo) {
        try {
            String[] lines = conteudo.split("\\r?\\n");
            int linesPerPage = 45;
            int numPages = (lines.length + linesPerPage - 1) / linesPerPage;
            if (numPages == 0) numPages = 1;

            List<List<String>> pages = new ArrayList<>();
            for (int i = 0; i < numPages; i++) {
                List<String> pageLines = new ArrayList<>();
                int start = i * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.length);
                for (int j = start; j < end; j++) {
                    pageLines.add(lines[j]);
                }
                pages.add(pageLines);
            }

            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
            
            // Header
            pdfOut.write("%PDF-1.4\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            // Binary indicator
            pdfOut.write("%\u00e2\u00e3\u00cf\u00d3\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

            int totalObjects = 3 + 2 * numPages;
            long[] offsets = new long[totalObjects + 1];

            List<byte[]> objects = new ArrayList<>();
            objects.add(new byte[0]); // Index 0 placeholder

            // Obj 1: Catalog
            String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
            objects.add(obj1.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

            // Obj 2: Pages
            StringBuilder sbPages = new StringBuilder();
            sbPages.append("2 0 obj\n<< /Type /Pages /Kids [");
            for (int i = 0; i < numPages; i++) {
                sbPages.append(3 + i).append(" 0 R ");
            }
            sbPages.append("] /Count ").append(numPages).append(" >>\nendobj\n");
            objects.add(sbPages.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

            int fontObjId = 3 + 2 * numPages;

            // Page Objects (3 to 2 + numPages)
            for (int i = 0; i < numPages; i++) {
                int pageObjId = 3 + i;
                int contentObjId = 3 + numPages + i;
                String objPage = pageObjId + " 0 obj\n" +
                        "<< /Type /Page\n" +
                        "   /Parent 2 0 R\n" +
                        "   /MediaBox [0 0 595.27 841.89]\n" +
                        "   /Resources << /Font << /F1 " + fontObjId + " 0 R >> >>\n" +
                        "   /Contents " + contentObjId + " 0 R\n" +
                        ">>\nendobj\n";
                objects.add(objPage.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            }

            // Page Content Streams (3 + numPages to 2 + 2*numPages)
            for (int i = 0; i < numPages; i++) {
                int contentObjId = 3 + numPages + i;
                List<String> pageLines = pages.get(i);
                
                ByteArrayOutputStream streamContent = new ByteArrayOutputStream();
                streamContent.write("BT\n/F1 10 Tf\n12 TL\n50 780 Td\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                for (String line : pageLines) {
                    String escaped = escapePdfString(line);
                    streamContent.write(("(" + escaped + ") Tj\nT*\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                }
                streamContent.write("ET\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                byte[] streamBytes = streamContent.toByteArray();

                ByteArrayOutputStream objStream = new ByteArrayOutputStream();
                String header = contentObjId + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n";
                objStream.write(header.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                objStream.write(streamBytes);
                objStream.write("\nendstream\nendobj\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                
                objects.add(objStream.toByteArray());
            }

            // Font Object
            String objFont = fontObjId + " 0 obj\n" +
                    "<< /Type /Font\n" +
                    "   /Subtype /Type1\n" +
                    "   /BaseFont /Courier\n" +
                    "   /Encoding /WinAnsiEncoding\n" +
                    ">>\nendobj\n";
            objects.add(objFont.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

            // Write all objects and record offsets
            for (int id = 1; id <= totalObjects; id++) {
                offsets[id] = pdfOut.size();
                pdfOut.write(objects.get(id));
            }

            // Xref table
            long xrefOffset = pdfOut.size();
            pdfOut.write("xref\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write(("0 " + (totalObjects + 1) + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write("0000000000 65535 f \n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            for (int id = 1; id <= totalObjects; id++) {
                String offsetStr = String.format("%010d", offsets[id]);
                pdfOut.write((offsetStr + " 00000 n \n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            }

            // Trailer
            pdfOut.write("trailer\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write(("<< /Size " + (totalObjects + 1) + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write("   /Root 1 0 R\n>>\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write("startxref\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write((xrefOffset + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            pdfOut.write("%%EOF\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

            return pdfOut.toByteArray();
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao gerar PDF: " + e.getMessage());
            return new byte[0];
        }
    }
}

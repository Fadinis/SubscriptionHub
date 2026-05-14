import model.*;
import controller.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   SUBSCRIPTION HUB - TESTE INTEGRAL DO SISTEMA  ");
        System.out.println("=================================================\n");

        // 1. SETUP DE CONTROLADORES
        ControladorAssinatura ctrlAssinatura = new ControladorAssinatura();
        ControladorRelatorio ctrlRelatorio = new ControladorRelatorio();
        ControladorAlerta ctrlAlerta = new ControladorAlerta();

        // 2. SETUP DE DADOS BASE
        Categoria streaming = new Categoria(1, "Streaming", "Entretenimento");
        Categoria trabalho = new Categoria(2, "Trabalho", "Produtividade");
        Categoria games = new Categoria(3, "Games", "Jogos");
        
        Usuario joao = new Usuario(1, "Joao Silva", "joao@email.com", "hash123");
        Usuario maria = new Usuario(3, "Maria Santos", "maria@email.com", "m123");
        Administrador admin = new Administrador(2, "Admin Master", "admin@hub.com", "admin_hash", 99);

        // --- [MODULO 1]: CADASTRO (Diagrama de Atividade c/ Loop de Correcao) ---
        System.out.println("--- [1] Módulo Cadastro: Teste de Retentativa ---");
        // Tentativa de cadastro invalido (nome vazio, valor negativo)
        Assinatura subInvalida = new Assinatura(1, "", -50.0, null, "", streaming, Periodicidade.MENSAL);
        ctrlAssinatura.cadastrarAssinatura(subInvalida, 2); // Tenta 2 vezes (corrigindo no loop)
        
        // Adicionando manualmente outras assinaturas para os testes
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Assinatura netflix = new Assinatura(2, "Netflix", 55.90, cal.getTime(), "Ativo", streaming, Periodicidade.MENSAL);
        
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Assinatura adobe = new Assinatura(3, "Adobe Cloud", 1200.00, cal.getTime(), "Ativo", trabalho, Periodicidade.ANUAL);
        
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Assinatura gamepass = new Assinatura(4, "Xbox GamePass", 45.00, cal.getTime(), "Ativo", games, Periodicidade.MENSAL);

        joao.getAssinaturas().add(netflix);
        joao.getAssinaturas().add(adobe);
        joao.getAssinaturas().add(gamepass);
        System.out.println();

        // --- [MODULO 2]: PAINEL CENTRAL (Diagrama de Atividade c/ Fork/Join) ---
        System.out.println("--- [2] Módulo Painel: Usuário SEM vs COM assinaturas ---");
        System.out.println(">> Caso Maria (Vazio):");
        ctrlAssinatura.acessarPainelCentral(maria.getId(), maria.getAssinaturas());
        
        System.out.println("\n>> Caso Joao (Com dados):");
        ctrlAssinatura.acessarPainelCentral(joao.getId(), joao.getAssinaturas());
        System.out.println();

        // --- [MODULO 3]: GERENCIAMENTO (Edicao e Exclusao) ---
        System.out.println("--- [3] Módulo Gerenciamento: Editar e Excluir ---");
        System.out.println(">> Editando Netflix para Premium:");
        Assinatura netflixPremium = new Assinatura(2, "Netflix Premium", 59.90, netflix.getDataVencimento(), "Ativo", streaming, Periodicidade.MENSAL);
        ctrlAssinatura.editarDados(netflix.getId(), netflixPremium, joao.getAssinaturas());
        
        System.out.println("\n>> Excluindo Adobe Cloud:");
        ctrlAssinatura.excluirAssinatura(adobe.getId(), joao.getAssinaturas());
        System.out.println();

        // --- [MODULO 4]: ORGANIZACAO (Filtrar por Categoria) ---
        System.out.println("--- [4] Módulo Organização: Agrupamento ---");
        // Adicionando Disney+ para ter mais de um streaming
        joao.getAssinaturas().add(new Assinatura(5, "Disney+", 33.90, new Date(), "Ativo", streaming, Periodicidade.MENSAL));
        
        Map<String, List<Assinatura>> agrupadas = ctrlAssinatura.organizarPorCategoria(joao.getAssinaturas());
        for (Map.Entry<String, List<Assinatura>> entry : agrupadas.entrySet()) {
            System.out.println("  Categoria [" + entry.getKey() + "]: " + entry.getValue().size() + " assinatura(s)");
        }
        System.out.println();

        // --- [MODULO 5]: RELATORIOS (Financeiro e Exportacao) ---
        System.out.println("--- [5] Módulo Financeiro: Relatórios ---");
        ctrlRelatorio.solicitarRelatorio(joao, 5, 2024);
        ctrlRelatorio.exportarRelatorio(joao.getRelatorios().get(0), "PDF");
        System.out.println();

        // --- [MODULO 6]: ALERTAS (Sistema de Notificacao) ---
        System.out.println("--- [6] Módulo Alertas: Verificação de Vencimentos ---");
        List<Usuario> usuariosSist = new ArrayList<>();
        usuariosSist.add(joao);
        ctrlAlerta.executarVerificacaoDiaria(usuariosSist, 7); // Alertas para os proximos 7 dias
        System.out.println();

        // --- [MODULO 7]: ADMIN & AUDITORIA ---
        System.out.println("--- [7] Módulo Auditoria: Administrador ---");
        admin.setLogs(ctrlAssinatura.getLogs());
        admin.auditarLogs();
        System.out.println();

        System.out.println("=================================================");
        System.out.println("         TESTE INTEGRAL CONCLUÍDO COM SUCESSO    ");
        System.out.println("=================================================");
    }
}
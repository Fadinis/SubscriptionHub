package com.subscriptionhub.notification;

import com.subscriptionhub.notification.client.SubscriptionClient;
import com.subscriptionhub.notification.client.SubscriptionDto;
import com.subscriptionhub.notification.model.Notification;
import com.subscriptionhub.notification.repository.NotificationRepository;
import com.subscriptionhub.notification.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * TAREFA JIRA: SH-109 — Testes Unitários do Notification Service
 * RESPONSÁVEL: Integrante 3 (Yago França)
 *
 * Casos de teste automatizados com JUnit 5 e Mockito simulando a integração
 * via REST com o Subscription Service e isolando a persistência de alertas.
 */
public class NotificationServiceTest {

    private NotificationRepository repositoryMock;
    private SubscriptionClient subscriptionClientMock;
    private NotificationService service;

    @BeforeEach
    public void setup() {
        // Inicializa mocks das dependências
        repositoryMock = Mockito.mock(NotificationRepository.class);
        subscriptionClientMock = Mockito.mock(SubscriptionClient.class);
        service = new NotificationService(repositoryMock, subscriptionClientMock);
    }

    @Test
    @DisplayName("UT-NS-01: Verificação de vencimento gera alerta quando assinatura vence dentro da janela")
    public void testVerificarVencimentosGeraAlerta() {
        // 1. Simula assinatura ativa que vence em 2 dias (dentro da janela de 3 dias)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date vencimentoProximo = cal.getTime();

        SubscriptionDto sub = new SubscriptionDto(1, 10, "Netflix 4K", 55.90, vencimentoProximo, "ATIVA");
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(10)).thenReturn(List.of(sub));
        Mockito.when(repositoryMock.save(Mockito.any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Executa a verificação com 3 dias de antecedência
        List<Notification> geradas = service.verificarVencimentosUsuario(10, 3);

        // 3. Valida se o alerta foi gerado e persistido corretamente
        Assertions.assertNotNull(geradas, "A lista de notificacoes geradas nao deve ser nula.");
        Assertions.assertEquals(1, geradas.size(), "Deveria gerar exatamente 1 alerta para a assinatura a vencer.");
        Assertions.assertEquals(10, geradas.get(0).getUsuarioId(), "O usuarioId da notificacao deve conferir.");
        Assertions.assertEquals(1, geradas.get(0).getAssinaturaId(), "O assinaturaId da notificacao deve ser 1.");
        Assertions.assertTrue(geradas.get(0).getMensagem().contains("Netflix 4K"), "A mensagem deve conter o nome do servico.");
        Assertions.assertTrue(geradas.get(0).getMensagem().contains("Alerta de Vencimento"), "A mensagem deve indicar alerta de vencimento.");

        // 4. Verifica chamadas aos mocks
        Mockito.verify(subscriptionClientMock, Mockito.times(1)).buscarPorUsuarioId(10);
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Notification.class));
    }

    @Test
    @DisplayName("UT-NS-02: Verificação não gera alerta quando nenhuma assinatura vence no prazo")
    public void testVerificarVencimentosNenhumAlerta() {
        // 1. Simula assinatura ativa que vence daqui a 25 dias (fora da janela de 3 dias)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 25);
        Date vencimentoDistante = cal.getTime();

        SubscriptionDto subDistante = new SubscriptionDto(2, 10, "Plano Anual Software", 299.90, vencimentoDistante, "ATIVA");
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(10)).thenReturn(List.of(subDistante));

        // 2. Executa a verificação com 3 dias de antecedência
        List<Notification> geradas = service.verificarVencimentosUsuario(10, 3);

        // 3. Valida que nenhum alerta foi gerado
        Assertions.assertNotNull(geradas);
        Assertions.assertTrue(geradas.isEmpty(), "Nao deve gerar alertas para assinaturas fora da janela de antecedencia.");

        // 4. Garante que repository.save NUNCA foi chamado
        Mockito.verify(subscriptionClientMock, Mockito.times(1)).buscarPorUsuarioId(10);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Notification.class));
    }

    @Test
    @DisplayName("UT-NS-03: Cálculo com dias de antecedência negativo deve ser rejeitado")
    public void testDiasAntecedenciaNegativo() {
        // 1. Executa verificação com dias de antecedência negativo (-2) esperando IllegalArgumentException
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.verificarVencimentosUsuario(10, -2),
                "Deveria ter lancado IllegalArgumentException para dias de antecedencia negativo."
        );

        // 2. Valida mensagem de erro e garante que nem o cliente REST nem o repository foram acionados
        Assertions.assertTrue(exception.getMessage().contains("negativo"), "A mensagem deve indicar valor negativo.");
        Mockito.verify(subscriptionClientMock, Mockito.never()).buscarPorUsuarioId(Mockito.anyInt());
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Notification.class));
    }

    @Test
    @DisplayName("UT-NS-04: Resiliência de comunicação quando Subscription Service está indisponível")
    public void testFalhaDeComunicacaoComSubscriptionService() {
        // 1. Simula falha/queda no microsserviço de assinaturas (exceção ao invocar o cliente HTTP)
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(10))
                .thenThrow(new IllegalStateException("Subscription Service fora do ar (conexao recusada na porta 8082)."));

        // 2. Executa a chamada esperando que o erro de comunicacao seja propagado de forma controlada
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> service.verificarVencimentosUsuario(10, 3),
                "Deveria propagar IllegalStateException informando indisponibilidade do servico externo."
        );

        // 3. Valida mensagem e garante que nenhuma notificacao orfa foi gravada
        Assertions.assertTrue(exception.getMessage().contains("fora do ar"));
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Notification.class));
    }
}

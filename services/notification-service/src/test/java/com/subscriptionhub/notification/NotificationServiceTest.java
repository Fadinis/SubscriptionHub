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
 * RESPONSÁVEL: Integrante 3
 *
 * INSTRUÇÕES:
 * 1. O Integrante 3 deve implementar os 4 casos de teste abaixo na sua branch: feature/notification-service.
 * 2. Utilize Mockito para simular a resposta do SubscriptionClient (simulando o microsserviço de assinaturas).
 * 3. Faça os commits com a mensagem: "SH-109: Implementa testes unitários de notificações com Mockito".
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
        // TODO: Integrante 3 deve implementar este teste:
        // 1. Criar uma assinatura mockada com vencimento em 2 dias (status "ATIVA").
        // 2. Simular o retorno de subscriptionClientMock.buscarPorUsuarioId(id).
        // 3. Executar service.verificarVencimentosUsuario(id, 3).
        // 4. Validar se 1 alerta foi gerado com mensagem coerente.
    }

    @Test
    @DisplayName("UT-NS-02: Verificação não gera alerta quando nenhuma assinatura vence no prazo")
    public void testVerificarVencimentosNenhumAlerta() {
        // TODO: Integrante 3 deve implementar este teste:
        // 1. Criar uma assinatura mockada com vencimento distante (ex: daqui a 25 dias).
        // 2. Executar service.verificarVencimentosUsuario(id, 3).
        // 3. Validar se a lista de alertas gerados é vazia (assertTrue(geradas.isEmpty())).
    }

    @Test
    @DisplayName("UT-NS-03: Cálculo com dias de antecedência negativo deve ser rejeitado")
    public void testDiasAntecedenciaNegativo() {
        // TODO: Integrante 3 deve implementar este teste:
        // 1. Chamar service.verificarVencimentosUsuario(id, -2) esperando IllegalArgumentException.
        // 2. Verificar que o subscriptionClient nunca foi chamado (Mockito.verify(..., Mockito.never())).
    }

    @Test
    @DisplayName("UT-NS-04: Resiliência de comunicação quando Subscription Service está indisponível")
    public void testFalhaDeComunicacaoComSubscriptionService() {
        // TODO: Integrante 3 deve implementar este teste:
        // 1. Simular queda de rede: Mockito.when(subscriptionClientMock.buscarPorUsuarioId(id)).thenThrow(new IllegalStateException(...))
        // 2. Verificar que service.verificarVencimentosUsuario trata ou propaga a exceção sem salvar notificações órfãs.
    }
}

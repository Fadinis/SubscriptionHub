package com.subscriptionhub.subscription;

import com.subscriptionhub.subscription.client.UserClient;
import com.subscriptionhub.subscription.client.UserDto;
import com.subscriptionhub.subscription.model.Periodicidade;
import com.subscriptionhub.subscription.model.Subscription;
import com.subscriptionhub.subscription.repository.SubscriptionRepository;
import com.subscriptionhub.subscription.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Optional;

/**
 * TAREFA JIRA: SH-108 — Testes Unitários do Subscription Service
 * RESPONSÁVEL: Integrante 2
 *
 * INSTRUÇÕES:
 * 1. O Integrante 2 deve implementar os 4 casos de teste abaixo na sua branch: feature/subscription-service.
 * 2. Utilize Mockito para simular a resposta do UserClient (simulando o microsserviço de usuários).
 * 3. Faça os commits com a mensagem: "SH-108: Implementa testes unitários de assinaturas com Mockito".
 */
public class SubscriptionServiceTest {

    private SubscriptionRepository repositoryMock;
    private UserClient userClientMock;
    private SubscriptionService service;

    @BeforeEach
    public void setup() {
        // Inicializa mocks das dependências (repositório e cliente HTTP do outro microsserviço)
        repositoryMock = Mockito.mock(SubscriptionRepository.class);
        userClientMock = Mockito.mock(UserClient.class);
        service = new SubscriptionService(repositoryMock, userClientMock);
    }

    @Test
    @DisplayName("UT-SS-01: Cadastro de assinatura com dados válidos e usuário existente")
    public void testCadastrarAssinaturaComSucesso() {
        // TODO: Integrante 2 deve implementar este teste:
        // 1. Simular que o UserClient encontra o usuário ID 1: Mockito.when(userClientMock.buscarUsuarioPorId(1)).thenReturn(...)
        // 2. Chamar service.cadastrar() com dados válidos de assinatura.
        // 3. Validar se o status é "ATIVA" e se a assinatura foi salva.
    }

    @Test
    @DisplayName("UT-SS-02: Cadastro com valor inválido (zero ou negativo) deve ser rejeitado")
    public void testCadastrarAssinaturaValorInvalido() {
        // TODO: Integrante 2 deve implementar este teste:
        // 1. Criar assinatura com valor 0.0 ou negativo.
        // 2. Usar Assertions.assertThrows(IllegalArgumentException.class, ...)
        // 3. Verificar que o repositoryMock.save nunca foi chamado (Mockito.verify(..., Mockito.never())).
    }

    @Test
    @DisplayName("UT-SS-03: Cadastro com usuário inexistente no User Service deve ser rejeitado")
    public void testCadastrarAssinaturaUsuarioInexistente() {
        // TODO: Integrante 2 deve implementar este teste:
        // 1. Simular que o UserClient NÃO encontra o usuário: Mockito.when(userClientMock.buscarUsuarioPorId(999)).thenReturn(Optional.empty())
        // 2. Tentar cadastrar e esperar IllegalArgumentException.
    }

    @Test
    @DisplayName("UT-SS-04: Cancelamento de assinatura ativa deve atualizar status para CANCELADA")
    public void testCancelarAssinatura() {
        // TODO: Integrante 2 deve implementar este teste:
        // 1. Simular uma assinatura ativa existente no repositoryMock.findById(id).
        // 2. Chamar service.cancelar(id).
        // 3. Validar se o status retornado passou a ser "CANCELADA".
    }
}

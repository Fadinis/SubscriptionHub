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
 * TAREFA JIRA: SH-108 (EDS-49) — Testes Unitários do Subscription Service
 * RESPONSÁVEL: Integrante 2 (Fabrício Saraiva)
 */
public class SubscriptionServiceTest {

    private SubscriptionRepository repositoryMock;
    private UserClient userClientMock;
    private SubscriptionService service;

    @BeforeEach
    public void setup() {
        repositoryMock = Mockito.mock(SubscriptionRepository.class);
        userClientMock = Mockito.mock(UserClient.class);
        service = new SubscriptionService(repositoryMock, userClientMock);
    }

    @Test
    @DisplayName("UT-SS-01: Cadastro de assinatura com dados válidos e usuário existente")
    public void testCadastrarAssinaturaComSucesso() {
        Subscription nova = new Subscription(0, 1, "Disney+", 39.90, new Date(), "ATIVA", "Streaming", Periodicidade.MENSAL);
        Subscription salva = new Subscription(10, 1, "Disney+", 39.90, new Date(), "ATIVA", "Streaming", Periodicidade.MENSAL);

        Mockito.when(userClientMock.buscarUsuarioPorId(1)).thenReturn(Optional.of(new UserDto(1, "Joao Silva", "joao@email.com")));
        Mockito.when(repositoryMock.save(Mockito.any(Subscription.class))).thenReturn(salva);

        Subscription resultado = service.cadastrar(nova);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(10, resultado.getId());
        Assertions.assertEquals("Disney+", resultado.getNomeServico());
        Assertions.assertEquals("ATIVA", resultado.getStatus());
        Mockito.verify(userClientMock, Mockito.times(1)).buscarUsuarioPorId(1);
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-02: Cadastro com valor inválido (zero ou negativo) deve ser rejeitado")
    public void testCadastrarAssinaturaValorInvalido() {
        Subscription subInvalida = new Subscription(0, 1, "Netflix", -10.0, new Date(), "ATIVA", "Streaming", Periodicidade.MENSAL);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar(subInvalida);
        });

        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-03: Cadastro com usuário inexistente no User Service deve ser rejeitado")
    public void testCadastrarAssinaturaUsuarioInexistente() {
        Subscription sub = new Subscription(0, 999, "Spotify", 21.90, new Date(), "ATIVA", "Musica", Periodicidade.MENSAL);

        Mockito.when(userClientMock.buscarUsuarioPorId(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar(sub);
        });

        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-04: Cancelamento de assinatura ativa deve atualizar status para CANCELADA")
    public void testCancelarAssinatura() {
        Subscription ativa = new Subscription(5, 1, "Amazon Prime", 19.90, new Date(), "ATIVA", "Streaming", Periodicidade.MENSAL);
        Mockito.when(repositoryMock.findById(5)).thenReturn(Optional.of(ativa));
        Mockito.when(repositoryMock.save(Mockito.any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription cancelada = service.cancelar(5);

        Assertions.assertNotNull(cancelada);
        Assertions.assertEquals("CANCELADA", cancelada.getStatus());
        Mockito.verify(repositoryMock, Mockito.times(1)).save(ativa);
    }
}

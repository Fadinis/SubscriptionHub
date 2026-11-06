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
 * GABARITO DE REFERÊNCIA - SUBSCRIPTION SERVICE
 * Use este arquivo apenas para consulta de sintaxe.
 */
public class SubscriptionServiceTestGabarito {

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
        Mockito.verify(userClientMock, Mockito.times(1)).buscarUsuarioPorId(1);
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-02: Cadastro com valor inválido (zero ou negativo) deve ser rejeitado")
    public void testCadastrarAssinaturaValorInvalido() {
        Subscription subValorZero = new Subscription(0, 1, "Servico Gratis?", 0.0, new Date(), "ATIVA", "Outros", Periodicidade.MENSAL);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(subValorZero)
        );

        Assertions.assertTrue(ex.getMessage().contains("maior que zero"));
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-03: Cadastro com usuário inexistente no User Service deve ser rejeitado")
    public void testCadastrarAssinaturaUsuarioInexistente() {
        Subscription sub = new Subscription(0, 999, "Servico X", 29.90, new Date(), "ATIVA", "Outros", Periodicidade.MENSAL);

        Mockito.when(userClientMock.buscarUsuarioPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(sub)
        );

        Assertions.assertTrue(ex.getMessage().contains("Usuario nao encontrado no User Service"));
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Subscription.class));
    }

    @Test
    @DisplayName("UT-SS-04: Cancelamento de assinatura ativa deve atualizar status para CANCELADA")
    public void testCancelarAssinatura() {
        Subscription existente = new Subscription(5, 1, "Revista Digital", 15.00, new Date(), "ATIVA", "Noticias", Periodicidade.MENSAL);
        Mockito.when(repositoryMock.findById(5)).thenReturn(Optional.of(existente));
        Mockito.when(repositoryMock.save(Mockito.any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription cancelada = service.cancelar(5);

        Assertions.assertEquals("CANCELADA", cancelada.getStatus());
        Mockito.verify(repositoryMock, Mockito.times(1)).save(existente);
    }
}


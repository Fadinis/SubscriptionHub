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
 * GABARITO DE REFERÊNCIA - NOTIFICATION SERVICE
 * Use este arquivo apenas para consulta de sintaxe.
 */
public class NotificationServiceTestGabarito {

    private NotificationRepository repositoryMock;
    private SubscriptionClient subscriptionClientMock;
    private NotificationService service;

    @BeforeEach
    public void setup() {
        repositoryMock = Mockito.mock(NotificationRepository.class);
        subscriptionClientMock = Mockito.mock(SubscriptionClient.class);
        service = new NotificationService(repositoryMock, subscriptionClientMock);
    }

    @Test
    @DisplayName("UT-NS-01: Verificação de vencimento gera alerta quando assinatura vence dentro da janela")
    public void testVerificarVencimentosGeraAlerta() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date vencimento = cal.getTime();

        SubscriptionDto sub = new SubscriptionDto(1, 10, "Amazon Prime", 19.90, vencimento, "ATIVA");
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(10)).thenReturn(List.of(sub));

        Mockito.when(repositoryMock.save(Mockito.any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification n = invocation.getArgument(0);
                    n.setId(100);
                    return n;
                });

        List<Notification> geradas = service.verificarVencimentosUsuario(10, 3);

        Assertions.assertEquals(1, geradas.size());
        Assertions.assertTrue(geradas.get(0).getMensagem().contains("Amazon Prime"));
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Notification.class));
    }

    @Test
    @DisplayName("UT-NS-02: Verificação não gera alerta quando nenhuma assinatura vence no prazo")
    public void testVerificarVencimentosNenhumAlerta() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 25);
        Date vencimento = cal.getTime();

        SubscriptionDto sub = new SubscriptionDto(2, 10, "Curso Online", 99.00, vencimento, "ATIVA");
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(10)).thenReturn(List.of(sub));

        List<Notification> geradas = service.verificarVencimentosUsuario(10, 3);

        Assertions.assertTrue(geradas.isEmpty());
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Notification.class));
    }

    @Test
    @DisplayName("UT-NS-03: Cálculo com dias de antecedência negativo deve ser rejeitado")
    public void testDiasAntecedenciaNegativo() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.verificarVencimentosUsuario(1, -2)
        );

        Assertions.assertTrue(ex.getMessage().contains("nao pode ser negativo"));
        Mockito.verify(subscriptionClientMock, Mockito.never()).buscarPorUsuarioId(Mockito.anyInt());
    }

    @Test
    @DisplayName("UT-NS-04: Resiliência de comunicação quando Subscription Service está indisponível")
    public void testFalhaDeComunicacaoComSubscriptionService() {
        Mockito.when(subscriptionClientMock.buscarPorUsuarioId(1))
                .thenThrow(new IllegalStateException("Nao foi possivel conectar ao Subscription Service"));

        IllegalStateException ex = Assertions.assertThrows(
                IllegalStateException.class,
                () -> service.verificarVencimentosUsuario(1, 3)
        );

        Assertions.assertTrue(ex.getMessage().contains("Subscription Service"));
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(Notification.class));
    }
}


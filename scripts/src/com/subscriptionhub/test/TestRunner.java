package com.subscriptionhub.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        SUBSCRIPTION HUB - EXECUCAO DE TESTES UNITARIOS AUTOMATIZADOS           ");
        System.out.println("================================================================================\n");

        List<Class<?>> testClasses = new ArrayList<>();

        if (args != null && args.length > 0) {
            // Execucao individual de uma classe especifica
            for (String className : args) {
                try {
                    testClasses.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    System.err.println("[ERRO] Classe de teste nao encontrada: " + className);
                }
            }
        } else {
            // Execucao geral de todas as classes
            testClasses.add(com.subscriptionhub.user.UserServiceTest.class);
            testClasses.add(com.subscriptionhub.subscription.SubscriptionServiceTest.class);
            testClasses.add(com.subscriptionhub.notification.NotificationServiceTest.class);
        }

        int totalGeral = 0;
        int passadosGeral = 0;
        int falhadosGeral = 0;
        long tempoInicio = System.currentTimeMillis();

        for (Class<?> testClass : testClasses) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("  Classe de Teste: " + testClass.getSimpleName());
            System.out.println("--------------------------------------------------------------------------------");

            Method beforeEachMethod = null;
            for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(BeforeEach.class)) {
                    beforeEachMethod = m;
                    break;
                }
            }

            for (Method testMethod : testClass.getDeclaredMethods()) {
                if (testMethod.isAnnotationPresent(Test.class)) {
                    totalGeral++;
                    String displayName = testMethod.getName();
                    if (testMethod.isAnnotationPresent(DisplayName.class)) {
                        displayName = testMethod.getAnnotation(DisplayName.class).value();
                    }

                    try {
                        Object instance = testClass.getDeclaredConstructor().newInstance();
                        if (beforeEachMethod != null) {
                            beforeEachMethod.invoke(instance);
                        }
                        testMethod.invoke(instance);
                        passadosGeral++;
                        System.out.println("  [PASSED] " + displayName);
                    } catch (InvocationTargetException ite) {
                        falhadosGeral++;
                        Throwable cause = ite.getCause();
                        System.err.println("  [FAILED] " + displayName);
                        System.err.println("           Motivo: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                    } catch (Exception e) {
                        falhadosGeral++;
                        System.err.println("  [ERROR]  " + displayName + " -> " + e.getMessage());
                    }
                }
            }
            System.out.println();
        }

        long tempoFim = System.currentTimeMillis();
        System.out.println("================================================================================");
        System.out.println(" RESUMO FINAL DA EXECUCAO DOS TESTES");
        System.out.println("================================================================================");
        System.out.println(" Total de Casos de Teste Executados: " + totalGeral);
        System.out.println(" Sucessos:                           " + passadosGeral);
        System.out.println(" Falhas:                             " + falhadosGeral);
        System.out.println(" Tempo Total:                        " + (tempoFim - tempoInicio) + " ms");
        System.out.println("================================================================================\n");

        if (falhadosGeral > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}

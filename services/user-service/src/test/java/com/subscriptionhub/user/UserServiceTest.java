package com.subscriptionhub.user;

import com.subscriptionhub.user.model.User;
import com.subscriptionhub.user.model.UserResponse;
import com.subscriptionhub.user.repository.UserRepository;
import com.subscriptionhub.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

/**
 * TAREFA JIRA: SH-107 — Testes Unitários do User Service
 * RESPONSÁVEL: Integrante 1
 *
 * INSTRUÇÕES:
 * 1. O Integrante 1 deve implementar os 4 casos de teste abaixo na sua branch: feature/user-service.
 * 2. Utilize JUnit 5 e Mockito para verificar as regras de negócio de maneira isolada.
 * 3. Faça os commits com a mensagem: "SH-107: Implementa testes unitários de usuário".
 */
public class UserServiceTest {

    private UserRepository repositoryMock;
    private UserService service;

    @BeforeEach
    public void setup() {
        // Inicializa o mock do repositório para isolar o teste da persistência em disco
        repositoryMock = Mockito.mock(UserRepository.class);
        service = new UserService(repositoryMock);
    }

    @Test
    @DisplayName("UT-US-01: Cadastro de usuário com dados válidos deve ter sucesso")
    public void testCadastrarUsuarioComSucesso() {
        // TODO: Integrante 1 deve implementar este teste:
        // 1. Criar um objeto User com dados válidos.
        // 2. Configurar o mock repositoryMock.findByEmail para retornar Optional.empty().
        // 3. Configurar repositoryMock.save para retornar o usuário com um novo ID.
        // 4. Executar service.cadastrar() e validar asserções de retorno (Assertions.assertNotNull, assertEquals).
    }

    @Test
    @DisplayName("UT-US-02: Cadastro com e-mail duplicado deve lançar exceção")
    public void testCadastrarUsuarioEmailDuplicado() {
        // TODO: Integrante 1 deve implementar este teste:
        // 1. Configurar repositoryMock.findByEmail com um e-mail já existente.
        // 2. Usar Assertions.assertThrows(IllegalArgumentException.class, ...) ao tentar cadastrar o mesmo e-mail.
        // 3. Validar se a mensagem de erro contém "ja cadastrado".
    }

    @Test
    @DisplayName("UT-US-03: Autenticação com credenciais corretas deve retornar usuário")
    public void testAutenticarCredenciaisCorretas() {
        // TODO: Integrante 1 deve implementar este teste:
        // 1. Configurar mock para retornar usuário com senha conhecida.
        // 2. Chamar service.autenticar(email, senhaCorreta).
        // 3. Validar se o Optional retornado está presente (assertTrue) e confere o nome/ID.
    }

    @Test
    @DisplayName("UT-US-04: Autenticação com senha incorreta deve falhar")
    public void testAutenticarSenhaIncorreta() {
        // TODO: Integrante 1 deve implementar este teste:
        // 1. Configurar mock para retornar usuário com uma senha.
        // 2. Chamar service.autenticar(email, "senhaErrada").
        // 3. Validar se o retorno é Optional.empty() (assertFalse).
    }
}

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
 * RESPONSÁVEL: Integrante 1 (Eduardo Fadini)
 *
 * Casos de teste automatizados com JUnit 5 + Mockito isolando a camada de persistência.
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
        // 1. Cria usuário de entrada (sem ID gerado ainda) e usuário simulado que seria salvo com ID 10
        User novo = new User(0, "Carlos Andrade", "carlos@teste.com", "senhaForte123");
        User salvo = new User(10, "Carlos Andrade", "carlos@teste.com", "senhaForte123");

        // 2. Configura o mock do repositório: e-mail ainda não existe na base
        Mockito.when(repositoryMock.findByEmail("carlos@teste.com")).thenReturn(Optional.empty());
        Mockito.when(repositoryMock.save(Mockito.any(User.class))).thenReturn(salvo);

        // 3. Executa a regra de negócio do serviço
        UserResponse response = service.cadastrar(novo);

        // 4. Asserções do JUnit 5
        Assertions.assertNotNull(response, "O retorno do cadastro nao deve ser nulo.");
        Assertions.assertEquals(10, response.getId(), "O ID do usuario cadastrado deve ser o retornado pelo banco.");
        Assertions.assertEquals("Carlos Andrade", response.getNome(), "O nome retornado deve ser preservado.");
        Assertions.assertEquals("carlos@teste.com", response.getEmail(), "O e-mail deve ser preservado.");

        // 5. Verifica se o método save foi invocado exatamente 1 vez
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("UT-US-02: Cadastro com e-mail duplicado deve lançar exceção")
    public void testCadastrarUsuarioEmailDuplicado() {
        // 1. Simula usuário pré-existente com o mesmo e-mail
        User usuarioExistente = new User(1, "Existente", "duplicado@teste.com", "12345");
        User tentativa = new User(0, "Novo", "duplicado@teste.com", "54321");

        // 2. Mock retorna usuário existente quando consultado por e-mail
        Mockito.when(repositoryMock.findByEmail("duplicado@teste.com")).thenReturn(Optional.of(usuarioExistente));

        // 3. Executa o cadastro e verifica se lança IllegalArgumentException
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(tentativa),
                "Deveria ter lancado IllegalArgumentException para e-mail duplicado."
        );

        // 4. Asserções da mensagem de erro e garantia de que o save NUNCA foi chamado
        Assertions.assertTrue(exception.getMessage().contains("ja cadastrado"), "Mensagem de erro deve indicar duplicidade de e-mail.");
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("UT-US-03: Autenticação com credenciais corretas deve retornar usuário")
    public void testAutenticarCredenciaisCorretas() {
        // 1. Configura usuário com e-mail e senha conhecidos
        User usuario = new User(1, "Maria Santos", "maria@email.com", "segredo123");
        Mockito.when(repositoryMock.findByEmail("maria@email.com")).thenReturn(Optional.of(usuario));

        // 2. Executa a autenticação com e-mail e senha corretos
        Optional<UserResponse> auth = service.autenticar("maria@email.com", "segredo123");

        // 3. Valida se o retorno está presente e contém as informações corretas
        Assertions.assertTrue(auth.isPresent(), "Autenticacao com credenciais validas deve ter sucesso.");
        Assertions.assertEquals(1, auth.get().getId(), "ID do usuario autenticado deve ser 1.");
        Assertions.assertEquals("Maria Santos", auth.get().getNome(), "Nome do usuario deve ser Maria Santos.");
        Assertions.assertEquals("maria@email.com", auth.get().getEmail(), "E-mail do usuario deve conferir.");
    }

    @Test
    @DisplayName("UT-US-04: Autenticação com senha incorreta deve falhar")
    public void testAutenticarSenhaIncorreta() {
        // 1. Configura usuário com senha cadastrada
        User usuario = new User(1, "Maria Santos", "maria@email.com", "segredo123");
        Mockito.when(repositoryMock.findByEmail("maria@email.com")).thenReturn(Optional.of(usuario));

        // 2. Tenta autenticar passando uma senha divergente
        Optional<UserResponse> auth = service.autenticar("maria@email.com", "senhaErrada123");

        // 3. Valida se o retorno é vazio
        Assertions.assertFalse(auth.isPresent(), "Autenticacao com senha incorreta deve retornar vazio.");
    }
}

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
 * GABARITO DE REFERÊNCIA - USER SERVICE
 * Use este arquivo apenas para consulta de sintaxe.
 */
public class UserServiceTestGabarito {

    private UserRepository repositoryMock;
    private UserService service;

    @BeforeEach
    public void setup() {
        repositoryMock = Mockito.mock(UserRepository.class);
        service = new UserService(repositoryMock);
    }

    @Test
    @DisplayName("UT-US-01: Cadastro de usuário com dados válidos deve ter sucesso")
    public void testCadastrarUsuarioComSucesso() {
        User novo = new User(0, "Carlos Andrade", "carlos@teste.com", "senhaForte123");
        User salvo = new User(10, "Carlos Andrade", "carlos@teste.com", "senhaForte123");

        Mockito.when(repositoryMock.findByEmail("carlos@teste.com")).thenReturn(Optional.empty());
        Mockito.when(repositoryMock.save(Mockito.any(User.class))).thenReturn(salvo);

        UserResponse response = service.cadastrar(novo);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(10, response.getId());
        Assertions.assertEquals("Carlos Andrade", response.getNome());
        Assertions.assertEquals("carlos@teste.com", response.getEmail());
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("UT-US-02: Cadastro com e-mail duplicado deve lançar exceção")
    public void testCadastrarUsuarioEmailDuplicado() {
        User usuarioExistente = new User(1, "Existente", "duplicado@teste.com", "12345");
        User tentativa = new User(0, "Novo", "duplicado@teste.com", "54321");

        Mockito.when(repositoryMock.findByEmail("duplicado@teste.com")).thenReturn(Optional.of(usuarioExistente));

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(tentativa)
        );

        Assertions.assertTrue(exception.getMessage().contains("ja cadastrado"));
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("UT-US-03: Autenticação com credenciais corretas deve retornar usuário")
    public void testAutenticarCredenciaisCorretas() {
        User usuario = new User(1, "Maria Santos", "maria@email.com", "segredo123");
        Mockito.when(repositoryMock.findByEmail("maria@email.com")).thenReturn(Optional.of(usuario));

        Optional<UserResponse> auth = service.autenticar("maria@email.com", "segredo123");

        Assertions.assertTrue(auth.isPresent());
        Assertions.assertEquals("Maria Santos", auth.get().getNome());
    }

    @Test
    @DisplayName("UT-US-04: Autenticação com senha incorreta deve falhar")
    public void testAutenticarSenhaIncorreta() {
        User usuario = new User(1, "Maria Santos", "maria@email.com", "segredo123");
        Mockito.when(repositoryMock.findByEmail("maria@email.com")).thenReturn(Optional.of(usuario));

        Optional<UserResponse> auth = service.autenticar("maria@email.com", "senhaErrada");

        Assertions.assertFalse(auth.isPresent());
    }
}


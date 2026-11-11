package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.GoogleTokenDTO;
import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.dto.TokenResponseDTO;
import com.escolar.gerenciadortarefas.service.AutentificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private AutentificacaoService autentificacaoService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(usuarioController, "restTemplate", restTemplate);
    }

    @Test
    void deveRealizarLoginComSucesso() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("teste@email.com");
        dto.setSenha("123456");

        TokenResponseDTO tokenMock = new TokenResponseDTO();
        tokenMock.setToken("jwt-token");
        tokenMock.setNomeUsuario("Aluno");

        when(autentificacaoService.loginTradicional(any(LoginDTO.class)))
                .thenReturn(tokenMock);

        ResponseEntity<TokenResponseDTO> resposta = usuarioController.login(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("jwt-token", resposta.getBody().getToken());
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        RegistroDTO dto = new RegistroDTO();
        dto.setNome("Aluno");
        dto.setEmail("teste@email.com");
        dto.setSenha("123456");

        TokenResponseDTO tokenMock = new TokenResponseDTO();
        tokenMock.setToken("jwt-registro");
        tokenMock.setNomeUsuario("Aluno");

        when(autentificacaoService.registrarUsuario(any(RegistroDTO.class)))
                .thenReturn(tokenMock);

        ResponseEntity<TokenResponseDTO> resposta = usuarioController.registro(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("jwt-registro", resposta.getBody().getToken());
    }

    @Test
    void deveRetornar401QuandoGoogleTokenForNuloOuVazio() {
        ResponseEntity<TokenResponseDTO> respNulo = usuarioController.loginGoogleReact(null);
        assertEquals(HttpStatus.UNAUTHORIZED, respNulo.getStatusCode());

        GoogleTokenDTO dtoVazio = new GoogleTokenDTO();
        dtoVazio.setIdToken("   ");
        ResponseEntity<TokenResponseDTO> respVazio = usuarioController.loginGoogleReact(dtoVazio);
        assertEquals(HttpStatus.UNAUTHORIZED, respVazio.getStatusCode());
    }

    @Test
    void deveAutenticarViaGoogleComSucesso() {
        GoogleTokenDTO dto = new GoogleTokenDTO();
        dto.setIdToken("token-google-valido");

        TokenResponseDTO tokenMock = new TokenResponseDTO();
        tokenMock.setToken("jwt-google");
        tokenMock.setNomeUsuario("Carla Kit");

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("email", "carla@gmail.com", "name", "Carla Kit"));
        when(autentificacaoService.loginGoogleReact("carla@gmail.com", "Carla Kit"))
                .thenReturn(tokenMock);

        ResponseEntity<TokenResponseDTO> resposta = usuarioController.loginGoogleReact(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("jwt-google", resposta.getBody().getToken());
    }

    @Test
    void deveAutenticarViaGoogleUsandoNomePadraoQuandoNameNaoVier() {
        GoogleTokenDTO dto = new GoogleTokenDTO();
        dto.setIdToken("token-google-sem-nome");

        TokenResponseDTO tokenMock = new TokenResponseDTO();
        tokenMock.setToken("jwt-google-2");
        tokenMock.setNomeUsuario("Usuário Google");

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("email", "semnome@gmail.com"));
        when(autentificacaoService.loginGoogleReact("semnome@gmail.com", "Usuário Google"))
                .thenReturn(tokenMock);

        ResponseEntity<TokenResponseDTO> resposta = usuarioController.loginGoogleReact(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Usuário Google", resposta.getBody().getNomeUsuario());
    }

    @Test
    void deveRetornar401QuandoGoogleNaoRetornarEmailOuLancarExcecao() {
        GoogleTokenDTO dto = new GoogleTokenDTO();
        dto.setIdToken("token-invalido");

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("sub", "12345"));
        assertEquals(HttpStatus.UNAUTHORIZED, usuarioController.loginGoogleReact(dto).getStatusCode());

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Erro de conexão"));
        assertEquals(HttpStatus.UNAUTHORIZED, usuarioController.loginGoogleReact(dto).getStatusCode());
    }
}

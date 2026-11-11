package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.dto.TokenResponseDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutentificacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AutentificacaoService autentificacaoService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Beatriz");
        usuarioMock.setEmail("beatriz@teste.com");
        usuarioMock.setSenha("senhaCriptografada");
        usuarioMock.setTelefone("11999999999");
        usuarioMock.setDataNascimento(LocalDate.of(2005, 5, 15));
    }

    @Test
    void deveRegistrarNovoUsuarioComSucesso() {
        RegistroDTO dto = new RegistroDTO();
        dto.setNome("Beatriz");
        dto.setEmail("Beatriz@Teste.com");
        dto.setSenha("123456");
        dto.setTelefone("11999999999");
        dto.setDataNascimento(LocalDate.of(2005, 5, 15));

        when(usuarioRepository.findByEmailIgnoreCase("beatriz@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        when(jwtService.generateToken(any(UsuarioUserDetails.class))).thenReturn("token-jwt-teste");

        TokenResponseDTO resposta = autentificacaoService.registrarUsuario(dto);

        assertNotNull(resposta);
        assertEquals("token-jwt-teste", resposta.getToken());
        assertEquals("Beatriz", resposta.getNomeUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("BEATRIZ@TESTE.COM");

        when(usuarioRepository.findByEmailIgnoreCase("beatriz@teste.com")).thenReturn(Optional.of(usuarioMock));

        assertThrows(IllegalArgumentException.class, () -> autentificacaoService.registrarUsuario(dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("Beatriz@Teste.com");
        dto.setSenha("123456");

        when(usuarioRepository.findByEmailIgnoreCase("beatriz@teste.com")).thenReturn(Optional.of(usuarioMock));
        when(jwtService.generateToken(any(UsuarioUserDetails.class))).thenReturn("token-login-valido");

        TokenResponseDTO resposta = autentificacaoService.loginTradicional(dto);

        assertNotNull(resposta);
        assertEquals("token-login-valido", resposta.getToken());
        assertEquals("Beatriz", resposta.getNomeUsuario());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void deveAutenticarViaGoogleCriandoNovoUsuarioSeNaoExistir() {
        when(usuarioRepository.findByEmailIgnoreCase("google@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senhaAleatoria");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(UsuarioUserDetails.class))).thenReturn("token-google-valido");

        TokenResponseDTO resposta = autentificacaoService.loginGoogleReact("Google@Teste.com", "Usuario Google");

        assertNotNull(resposta);
        assertEquals("token-google-valido", resposta.getToken());
        assertEquals("Usuario Google", resposta.getNomeUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}

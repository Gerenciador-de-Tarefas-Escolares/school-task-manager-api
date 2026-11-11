package com.escolar.gerenciadortarefas.security;

import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.service.JwtService;
import com.escolar.gerenciadortarefas.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityPackageTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    private Usuario usuario;
    private UsuarioUserDetails userDetails;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Maria");
        usuario.setEmail("maria@teste.com");
        usuario.setSenha("senha123");
        userDetails = new UsuarioUserDetails(usuario);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveTestarUsuarioUserDetails() {
        assertEquals("maria@teste.com", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void deveTestarSecureJwtProxyServiceRevogacao() {
        SecureJwtProxyService proxy = new SecureJwtProxyService();
        ReflectionTestUtils.setField(proxy, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(proxy, "jwtExpiration", 3600000L);

        String token = proxy.generateToken(userDetails);
        assertTrue(proxy.validarToken(token, userDetails));

        proxy.revogarToken(token);
        assertFalse(proxy.validarToken(token, userDetails));
        assertFalse(proxy.validarToken(null, userDetails));
    }

    @Test
    void deveTestarJwtAuthenticationFilterComHeaderValidoESemHeader() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        MockHttpServletRequest reqSemToken = new MockHttpServletRequest();
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        filter.doFilterInternal(reqSemToken, res1, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        MockHttpServletRequest reqComToken = new MockHttpServletRequest();
        reqComToken.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse res2 = new MockHttpServletResponse();

        when(jwtService.extrairUsername("token-valido")).thenReturn("maria@teste.com");
        when(userDetailsService.loadUserByUsername("maria@teste.com")).thenReturn(userDetails);
        when(jwtService.validarToken("token-valido", userDetails)).thenReturn(true);

        filter.doFilterInternal(reqComToken, res2, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(2)).doFilter(any(), any());
    }
}

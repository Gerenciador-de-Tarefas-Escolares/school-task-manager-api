package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MateriaControllerTest {

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private MateriaController materiaController;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveListarMateriasDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("aluno@teste.com");
        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuario);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Materia m = new Materia();
        m.setId(1L);
        m.setNome("Matemática");

        when(materiaRepository.findByUsuarioId(1L)).thenReturn(List.of(m));

        ResponseEntity<List<Materia>> resposta = materiaController.listar();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().size());
        assertEquals("Matemática", resposta.getBody().get(0).getNome());
    }
}

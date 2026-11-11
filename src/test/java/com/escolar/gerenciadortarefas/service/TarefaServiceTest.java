package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Usuario usuarioMock;
    private Materia materiaMock;
    private Tarefa tarefaMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Samantha");
        usuarioMock.setEmail("samantha@teste.com");

        materiaMock = new Materia();
        materiaMock.setId(10L);
        materiaMock.setNome("Matemática");
        materiaMock.setUsuario(usuarioMock);

        tarefaMock = new Tarefa();
        tarefaMock.setId(100L);
        tarefaMock.setTitulo("Lista de Exercícios");
        tarefaMock.setStatus("PENDENTE");
        tarefaMock.setPrioridade("MEDIA");
        tarefaMock.setUsuario(usuarioMock);
        tarefaMock.setMateria(materiaMock);

        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuarioMock);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveListarTarefasDoUsuario() {
        when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefaMock));

        List<Tarefa> resultado = tarefaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Lista de Exercícios", resultado.get(0).getTitulo());
    }

    @Test
    void deveCriarTarefaComNovaMateria() {
        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Redação");
        dto.setNovaMateriaNome("Português");
        dto.setDataEntrega(LocalDateTime.now().plusDays(3));

        Materia novaMateria = new Materia();
        novaMateria.setId(20L);
        novaMateria.setNome("Português");
        novaMateria.setUsuario(usuarioMock);

        when(materiaRepository.save(any(Materia.class))).thenReturn(novaMateria);
        when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(inv -> inv.getArgument(0));

        Tarefa criada = tarefaService.salvar(dto);

        assertNotNull(criada);
        assertEquals("Redação", criada.getTitulo());
        assertEquals("Português", criada.getMateria().getNome());
        assertEquals("PENDENTE", criada.getStatus());
    }

    @Test
    void deveAtualizarTarefaDoProprioUsuario() {
        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Lista Atualizada");
        dto.setStatus("CONCLUIDA");

        when(tarefaRepository.findById(100L)).thenReturn(Optional.of(tarefaMock));
        when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(inv -> inv.getArgument(0));

        Tarefa atualizada = tarefaService.atualizar(100L, dto);

        assertEquals("Lista Atualizada", atualizada.getTitulo());
        assertEquals("CONCLUIDA", atualizada.getStatus());
        assertNotNull(atualizada.getDataConclusao());
    }

    @Test
    void deveLancarExcecaoAoAtualizarTarefaDeOutroUsuario() {
        Usuario invasor = new Usuario();
        invasor.setId(999L);
        UsuarioUserDetails invasorDetails = new UsuarioUserDetails(invasor);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(invasorDetails, null, invasorDetails.getAuthorities())
        );

        when(tarefaRepository.findById(100L)).thenReturn(Optional.of(tarefaMock));

        assertThrows(RuntimeException.class, () ->
                tarefaService.atualizar(100L, new TarefaDTO())
        );
    }

    @Test
    void deveExcluirTarefaDoProprioUsuario() {
        when(tarefaRepository.findById(100L)).thenReturn(Optional.of(tarefaMock));

        tarefaService.deletar(100L);

        verify(tarefaRepository, times(1)).delete(tarefaMock);
    }
}

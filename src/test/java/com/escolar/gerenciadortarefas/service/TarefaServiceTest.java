package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Tarefa tarefa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Beatriz");
        usuario.setEmail("beatriz@email.com");

        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setMateria("Programação");
        tarefa.setDataEntrega(LocalDate.now().plusDays(1));
        tarefa.setConcluida(false);
        tarefa.setUsuario(usuario);
    }

    @Test
    void deveListarTarefasPorUsuarioComSucesso() {
        when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefa));

        List<Tarefa> resultado = tarefaService.listarPorUsuario(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Estudar Spring Boot", resultado.get(0).getTitulo());
        verify(tarefaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveSalvarTarefaAtribuindoConcluidaFalseNula() {
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo("Criar documentação");
        novaTarefa.setMateria("Arquitetura");
        novaTarefa.setDataEntrega(LocalDate.now());
        novaTarefa.setConcluida(null);

        Tarefa resultado = tarefaService.salvar(novaTarefa);

        assertNotNull(resultado);
        assertFalse(resultado.getConcluida());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    void deveLancarExecaoQuandoTarefaNaoEcontradaPorId() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tarefaService.buscarPorId(99L);
        });
    }

    @Test
    void deveDeletarTarefaComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        doNothing().when(tarefaRepository).delete(tarefa);

        assertDoesNotThrow(() -> tarefaService.deletar(1L));
        verify(tarefaRepository, times(1)).delete(tarefa);
    }
}

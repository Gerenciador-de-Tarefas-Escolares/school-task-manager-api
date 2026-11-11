package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TarefaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TarefaService tarefaService;

    @InjectMocks
    private TarefaController tarefaController;

    private Tarefa tarefaMock;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(tarefaController).build();

        tarefaMock = new Tarefa();
        tarefaMock.setId(1L);
        tarefaMock.setTitulo("Estudar Java");
        tarefaMock.setDescricao("Estudar Spring Boot");
        tarefaMock.setStatus("PENDENTE");
        tarefaMock.setPrioridade("ALTA");
        tarefaMock.setDataEntrega(LocalDateTime.of(2026, 12, 10, 23, 59));
    }

    @Test
    void deveListarTarefasDoUsuario() throws Exception {
        when(tarefaService.listarTodas()).thenReturn(List.of(tarefaMock));

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Estudar Java"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"));
    }

    @Test
    void deveCriarTarefaComSucesso() throws Exception {
        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Estudar Java");
        dto.setDescricao("Estudar Spring Boot");
        dto.setPrioridade("ALTA");
        dto.setDataEntrega(LocalDateTime.of(2026, 12, 10, 23, 59));

        when(tarefaService.salvar(any(TarefaDTO.class))).thenReturn(tarefaMock);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Estudar Java"));
    }

    @Test
    void deveAtualizarTarefaComSucesso() throws Exception {
        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Estudar Java Avançado");
        dto.setStatus("CONCLUIDA");

        tarefaMock.setTitulo("Estudar Java Avançado");
        tarefaMock.setStatus("CONCLUIDA");

        when(tarefaService.atualizar(eq(1L), any(TarefaDTO.class))).thenReturn(tarefaMock);

        mockMvc.perform(put("/api/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Estudar Java Avançado"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }

    @Test
    void deveExcluirTarefaComSucesso() throws Exception {
        doNothing().when(tarefaService).deletar(1L);

        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());

        verify(tarefaService, times(1)).deletar(1L);
    }
}

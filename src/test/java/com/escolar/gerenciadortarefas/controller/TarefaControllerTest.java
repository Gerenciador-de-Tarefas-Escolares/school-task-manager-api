package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import com.escolar.gerenciadortarefas.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TarefaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Usuario usuarioPadrao;

    @Mock
    private TarefaService tarefaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TarefaController tarefaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        usuarioPadrao = new Usuario();
        usuarioPadrao.setId(4L);
        usuarioPadrao.setEmail("sofia@email.com");

        mockMvc = MockMvcBuilders.standaloneSetup(tarefaController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(org.springframework.security.core.annotation.AuthenticationPrincipal.class) != null;
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return new User(usuarioPadrao.getEmail(), "senha123", List.of());
                    }
                })
                .build();
    }

    private void simularUtilizadorLogado() {
        when(usuarioRepository.findByEmail(usuarioPadrao.getEmail())).thenReturn(Optional.of(usuarioPadrao));
    }

    @Test
    void deveListarTarefasDoUsuarioComSucesso() throws Exception {
        simularUtilizadorLogado();

        Tarefa tarefa = new Tarefa();
        tarefa.setId(4L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setMateria("Programação");
        tarefa.setDataEntrega(LocalDate.now());
        tarefa.setConcluida(false);

        when(tarefaService.listarPorUsuario(4L)).thenReturn(List.of(tarefa));

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Estudar Spring Boot"));
    }

    @Test
    void deveCriarTarefaComSucesso() throws Exception {
        simularUtilizadorLogado();

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo("Criar Testes Unitários");
        novaTarefa.setMateria("Qualidade de Software");
        novaTarefa.setDataEntrega(LocalDate.now().plusDays(2));
        novaTarefa.setConcluida(false);

        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setTitulo(novaTarefa.getTitulo());
        tarefaSalva.setMateria(novaTarefa.getMateria());
        tarefaSalva.setDataEntrega(novaTarefa.getDataEntrega());
        tarefaSalva.setConcluida(novaTarefa.getConcluida());

        when(tarefaService.salvar(any(Tarefa.class))).thenReturn(tarefaSalva);

        mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaTarefa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.titulo").value("Criar Testes Unitários"));
    }

    @Test
    void deveAtualizarTarefaComSucesso() throws Exception {
        Tarefa tarefaAtualizada = new Tarefa();
        tarefaAtualizada.setId(1L);
        tarefaAtualizada.setTitulo("Estudar Spring Boot Avançado");
        tarefaAtualizada.setMateria("Programação");
        tarefaAtualizada.setDataEntrega(LocalDate.now().plusDays(1));
        tarefaAtualizada.setConcluida(true);

        when(tarefaService.atualizar(eq(1L), any(Tarefa.class))).thenReturn(tarefaAtualizada);

        mockMvc.perform(put("/api/tarefas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Estudar Spring Boot Avançado"))
                .andExpect(jsonPath("$.concluida").value(true));
    }

    @Test
    void deveDeletarTarefaComSucesso() throws Exception {
        doNothing().when(tarefaService).deletar(1L);

        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());
    }
}

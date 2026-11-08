package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public List<Tarefa> listarPorUsuario(Long usuarioId) {
        return tarefaRepository.findByUsuarioId(usuarioId);
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + id));
    }

    public Tarefa salvar(TarefaDTO dto, Usuario usuario) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setDataEntrega(dto.getDataEntrega());
        tarefa.setUsuario(usuario);

        if (dto.getMateriaId() != null) {
            tarefa.setMateriaId(dto.getMateriaId());
        }

        tarefa.setPrioridade(dto.getPrioridade() != null ? dto.getPrioridade() : "MEDIA");

        if ("CONCLUIDA".equalsIgnoreCase(dto.getStatus()) || (dto.getConcluida() != null && dto.getConcluida())) {
            tarefa.setStatus("CONCLUIDA");
            tarefa.setDataConclusao(LocalDateTime.now());
        } else {
            tarefa.setStatus("PENDENTE");
        }

        return tarefaRepository.save(tarefa);
    }

    public Tarefa atualizar(Long id, TarefaDTO dto) {
        Tarefa tarefaExistente = buscarPorId(id);

        tarefaExistente.setTitulo(dto.getTitulo());
        tarefaExistente.setDescricao(dto.getDescricao());
        tarefaExistente.setDataEntrega(dto.getDataEntrega());

        if (dto.getMateriaId() != null) {
            tarefaExistente.setMateriaId(dto.getMateriaId());
        }

        if (dto.getPrioridade() != null) {
            tarefaExistente.setPrioridade(dto.getPrioridade());
        }

        if ("CONCLUIDA".equalsIgnoreCase(dto.getStatus()) || (dto.getConcluida() != null && dto.getConcluida())) {
            tarefaExistente.setStatus("CONCLUIDA");
            if (tarefaExistente.getDataConclusao() == null) {
                tarefaExistente.setDataConclusao(LocalDateTime.now());
            }
        } else {
            tarefaExistente.setStatus("PENDENTE");
            tarefaExistente.setDataConclusao(null);
        }

        return tarefaRepository.save(tarefaExistente);
    }

    public void deletar(Long id) {
        Tarefa tarefa = buscarPorId(id);
        tarefaRepository.delete(tarefa);
    }
}
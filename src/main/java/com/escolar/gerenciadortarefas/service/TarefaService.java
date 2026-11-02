package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodos() {
        return tarefaRepository.findAll();
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + id));
    }

    public Tarefa salvar(Tarefa tarefa) {
        if (tarefa.getConcluida() == null) {
            tarefa.setConcluida(false);
        }
        return tarefaRepository.save(tarefa);
    }

    public Tarefa atualizar(Long id, Tarefa tarefaAtualizada) {
        Tarefa tarefaExistente = buscarPorId(id);

        tarefaExistente.setTitulo(tarefaAtualizada.getTitulo());
        tarefaExistente.setMateria(tarefaAtualizada.getMateria());
        tarefaExistente.setDataEntrega(tarefaAtualizada.getDataEntrega());

        if (tarefaAtualizada.getConcluida() != null) {
            tarefaExistente.setConcluida(tarefaAtualizada.getConcluida());
        }
        return tarefaRepository.save(tarefaExistente);
    }

    public void deletar(Long id) {
        Tarefa tarefa = buscarPorId(id);
        tarefaRepository.delete(tarefa);
    }
}

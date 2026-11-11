package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final MateriaRepository materiaRepository;

    private Usuario getUsuarioLogado() {
        UsuarioUserDetails userDetails = (UsuarioUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUsuario();
    }

    @Transactional
    public Tarefa salvar(TarefaDTO dto) {
        Usuario usuarioLogado = getUsuarioLogado();

        Tarefa tarefa = new Tarefa();
        tarefa.setUsuario(usuarioLogado);
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setDataEntrega(dto.getDataEntrega());
        tarefa.setPrioridade(dto.getPrioridade() != null ? dto.getPrioridade() : "MEDIA");
        tarefa.setStatus("PENDENTE");
        tarefa.setDataCriacao(LocalDateTime.now());

        vincularMateria(tarefa, dto, usuarioLogado);

        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa atualizar(Long id, TarefaDTO dto) {
        Usuario usuarioLogado = getUsuarioLogado();
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!tarefa.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Acesso negado a esta tarefa.");
        }

        if (dto.getTitulo() != null) tarefa.setTitulo(dto.getTitulo());
        if (dto.getDescricao() != null) tarefa.setDescricao(dto.getDescricao());
        if (dto.getDataEntrega() != null) tarefa.setDataEntrega(dto.getDataEntrega());
        if (dto.getPrioridade() != null) tarefa.setPrioridade(dto.getPrioridade());

        if (dto.getStatus() != null) {
            tarefa.setStatus(dto.getStatus());
            if ("CONCLUIDA".equals(dto.getStatus())) {
                tarefa.setDataConclusao(LocalDateTime.now());
            } else {
                tarefa.setDataConclusao(null);
            }
        }

        vincularMateria(tarefa, dto, usuarioLogado);

        return tarefaRepository.save(tarefa);
    }

    private void vincularMateria(Tarefa tarefa, TarefaDTO dto, Usuario usuarioLogado) {
        if (dto.getNovaMateriaNome() != null && !dto.getNovaMateriaNome().trim().isEmpty()) {
            Materia novaMateria = new Materia();
            novaMateria.setUsuario(usuarioLogado);
            novaMateria.setNome(dto.getNovaMateriaNome().trim());
            novaMateria.setCor("#3b82f6");

            Materia materiaSalva = materiaRepository.save(novaMateria);
            tarefa.setMateria(materiaSalva);
        } else if (dto.getMateriaId() != null) {
            Materia materiaExistente = materiaRepository.findById(dto.getMateriaId())
                    .orElseThrow(() -> new RuntimeException("Matéria escolar não encontrada no banco de dados."));
            tarefa.setMateria(materiaExistente);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> listarTodas() {
        return tarefaRepository.findByUsuarioId(getUsuarioLogado().getId());
    }

    @Transactional
    public Tarefa atualizarStatus(Long id, String novoStatus) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        tarefa.setStatus(novoStatus);

        if ("CONCLUIDA".equals(novoStatus)) {
            tarefa.setDataConclusao(LocalDateTime.now());
        } else {
            tarefa.setDataConclusao(null);
        }

        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!tarefa.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Acesso negado a esta tarefa.");
        }

        tarefaRepository.delete(tarefa);
    }
}

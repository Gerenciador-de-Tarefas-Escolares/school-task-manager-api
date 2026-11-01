package com.escolar.tarefasgerenciador.repository;

import com.escolar.tarefasgerenciador.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>{
    List<Tarefa> findByUsuarioId(Long usuarioId);
}

package com.escolar.gerenciadortarefas.repository;

import com.escolar.gerenciadortarefas.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByUsuarioIdOrderByDataAvaliacaoAsc(Long usuarioId);
    Optional<Avaliacao> findByIdAndUsuarioId(Long id, Long usuarioId);
}

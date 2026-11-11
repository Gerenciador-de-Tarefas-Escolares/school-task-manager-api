package com.escolar.gerenciadortarefas.repository;

import com.escolar.gerenciadortarefas.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
    List<Materia> findByUsuarioId(Long usuarioId);
}

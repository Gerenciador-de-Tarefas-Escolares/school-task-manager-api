package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
@Tag(name = "Matérias", description = "Consulta das matérias escolares cadastradas pelo usuário")
public class MateriaController {

    private final MateriaRepository materiaRepository;

    @Operation(summary = "Listar matérias", description = "Retorna todas as matérias vinculadas ao usuário autenticado")
    @GetMapping
    public ResponseEntity<List<Materia>> listar() {
        UsuarioUserDetails userDetails = (UsuarioUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        List<Materia> materias = materiaRepository.findByUsuarioId(userDetails.getUsuario().getId());
        return ResponseEntity.ok(materias);
    }
}

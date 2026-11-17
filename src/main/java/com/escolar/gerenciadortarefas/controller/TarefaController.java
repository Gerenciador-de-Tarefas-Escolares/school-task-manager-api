package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Gerenciamento das tarefas escolares do usuário autenticado")
public class TarefaController {

    private final TarefaService tarefaService;

    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa vinculada ao usuário autenticado")
    @PostMapping
    public ResponseEntity<Tarefa> criar(@RequestBody TarefaDTO dto) {
        return ResponseEntity.ok(tarefaService.salvar(dto));
    }

    @Operation(summary = "Listar tarefas", description = "Retorna todas as tarefas do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<Tarefa>> listar() {
        return ResponseEntity.ok(tarefaService.listarTodas());
    }

    @Operation(summary = "Atualizar tarefa", description = "Atualiza dados ou status de conclusão de uma tarefa existente")
    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizar(@PathVariable Long id, @RequestBody TarefaDTO dto) {
        return ResponseEntity.ok(tarefaService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir tarefa", description = "Remove uma tarefa pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

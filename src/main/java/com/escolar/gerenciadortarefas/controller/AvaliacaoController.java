package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.AvaliacaoDTO;
import com.escolar.gerenciadortarefas.model.Avaliacao;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import com.escolar.gerenciadortarefas.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@Tag(name = "Avaliações", description = "Gerenciamento de provas, trabalhos, seminários e notas")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @GetMapping
    @Operation(summary = "Listar avaliações", description = "Lista todas as avaliações do usuário autenticado")
    public ResponseEntity<List<Avaliacao>> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return ResponseEntity.ok(avaliacaoService.listarPorUsuario(userDetails.getUsuario().getId()));
    }

    @PostMapping
    @Operation(summary = "Criar avaliação", description = "Cadastra uma nova prova, trabalho ou seminário")
    public ResponseEntity<Avaliacao> criar(
            @RequestBody @Valid AvaliacaoDTO dto,
            @AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return ResponseEntity.ok(avaliacaoService.criar(dto, userDetails.getUsuario().getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar avaliação", description = "Atualiza dados ou lança a nota de uma avaliação existente")
    public ResponseEntity<Avaliacao> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AvaliacaoDTO dto,
            @AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return ResponseEntity.ok(avaliacaoService.atualizar(id, dto, userDetails.getUsuario().getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação", description = "Remove uma avaliação do usuário autenticado")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioUserDetails userDetails) {
        avaliacaoService.excluir(id, userDetails.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }
}

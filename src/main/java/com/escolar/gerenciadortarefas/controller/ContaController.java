package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.RecuperarSenhaDTO;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import com.escolar.gerenciadortarefas.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@Tag(name = "Conta do Usuário", description = "Endpoints para recuperação de senha e exclusão de conta")
public class ContaController {

    private final ContaService contaService;

    @Operation(summary = "Solicitar recuperação de senha")
    @PostMapping("/api/auth/esqueceu-senha")
    public ResponseEntity<Map<String, String>> esqueceuSenha(@RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(contaService.solicitarRecuperacaoSenha(payload.get("email")));
    }

    @Operation(summary = "Redefinir senha com token")
    @PostMapping("/api/auth/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@RequestBody RecuperarSenhaDTO dto) {
        contaService.redefinirSenha(dto);
        return ResponseEntity.ok(Map.of("message", "Senha atualizada com sucesso!"));
    }

    @Operation(summary = "Excluir conta do usuário logado")
    @DeleteMapping("/api/conta")
    public ResponseEntity<Void> excluirConta(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        contaService.excluirConta(userDetails);
        return ResponseEntity.noContent().build();
    }
}

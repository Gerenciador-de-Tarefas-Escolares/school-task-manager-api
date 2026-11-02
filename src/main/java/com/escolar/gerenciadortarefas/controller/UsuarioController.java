package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.service.AutentificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UsuarioController {

    private final AutentificacaoService autentificacaoService;

    @PostMapping("/registrar")
    public Usuario registrar(@RequestBody RegistroDTO registroDTO) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registroDTO.getNome());
        novoUsuario.setEmail(registroDTO.getEmail());
        novoUsuario.setSenha(registroDTO.getSenha());

        return autentificacaoService.registrar(novoUsuario);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginDTO loginDTO) {
        return autentificacaoService.login(loginDTO.getEmail(), loginDTO.getSenha());
    }
}

package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutentificacaoService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está registrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha Incorreta");
        }
        return usuario;
    }

    public Usuario processarLoginGoogle(String email, String nome) {
        return usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setEmail(email);
            novoUsuario.setNome(nome);
            novoUsuario.setSenha(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
            return usuarioRepository.save(novoUsuario);
        });
    }
}

package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.dto.TokenResponseDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutentificacaoService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenResponseDTO registrarUsuario(RegistroDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        if (usuarioRepository.findByEmailIgnoreCase(emailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado no sistema.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(emailNormalizado);
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setTelefone(dto.getTelefone());
        novoUsuario.setDataNascimento(dto.getDataNascimento());

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuarioSalvo);
        String jwtToken = jwtService.generateToken(userDetails);

        TokenResponseDTO response = new TokenResponseDTO();
        response.setToken(jwtToken);
        response.setNomeUsuario(usuarioSalvo.getNome());
        return response;
    }

    public TokenResponseDTO loginTradicional(LoginDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailNormalizado, dto.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuario);
        String jwtToken = jwtService.generateToken(userDetails);

        TokenResponseDTO response = new TokenResponseDTO();
        response.setToken(jwtToken);
        response.setNomeUsuario(usuario.getNome());
        return response;
    }

    public TokenResponseDTO loginGoogleReact(String email, String nome) {
        String emailNormalizado = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setNome(nome);
                    novo.setEmail(emailNormalizado);
                    novo.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return usuarioRepository.save(novo);
                });

        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuario);
        String jwtToken = jwtService.generateToken(userDetails);

        TokenResponseDTO response = new TokenResponseDTO();
        response.setToken(jwtToken);
        response.setNomeUsuario(usuario.getNome());
        return response;
    }
}

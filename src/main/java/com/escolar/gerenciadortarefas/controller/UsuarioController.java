package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.dto.TokenResponseDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.service.JwtService;
import com.escolar.gerenciadortarefas.service.AutentificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UsuarioController {

    private final AutentificacaoService autentificacaoService;
    private final JwtService jwtService;
    private final com.escolar.gerenciadortarefas.service.UserDetailsServiceImpl userDetailsService;

    @PostMapping("/registrar")
    public ResponseEntity<TokenResponseDTO> registrar(@RequestBody RegistroDTO registroDTO) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registroDTO.getNome());
        novoUsuario.setEmail(registroDTO.getEmail());
        novoUsuario.setSenha(registroDTO.getSenha());

        Usuario usuarioSalvo = autentificacaoService.registrar(novoUsuario);
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuarioSalvo.getEmail());
        String token = jwtService.gerarToken(userDetails);

        return ResponseEntity.ok(new TokenResponseDTO(token, usuarioSalvo.getNome(), usuarioSalvo.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Usuario usuario = autentificacaoService.login(loginDTO.getEmail(), loginDTO.getSenha());
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.gerarToken(userDetails);

        return ResponseEntity.ok(new TokenResponseDTO(token, usuario.getNome(), usuario.getEmail()));
    }

    @GetMapping("/google-sucesso")
    public ResponseEntity<TokenResponseDTO> googleSucesso(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new RuntimeException("Falha na autentificação com o Google.");
        }

            String email = oAuth2User.getAttribute("email");
            String nome = oAuth2User.getAttribute("name");

            Usuario usuario = autentificacaoService.processarLoginGoogle(email, nome);
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            String token = jwtService.gerarToken(userDetails);

            return ResponseEntity.ok(new TokenResponseDTO(token, usuario.getNome(), usuario.getEmail()));
    }
}

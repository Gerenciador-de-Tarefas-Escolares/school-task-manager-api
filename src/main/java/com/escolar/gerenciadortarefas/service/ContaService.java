package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.RecuperarSenhaDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, String> solicitarRecuperacaoSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado no sistema."));

        String token = Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("tipo", "RECUPERACAO_SENHA")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of(
                "message", "Token de recuperação gerado com sucesso.",
                "tokenRecuperacao", token
        );
    }

    @Transactional
    public void redefinirSenha(RecuperarSenhaDTO dto) {
        if (dto.getTokenRecuperacao() == null || dto.getTokenRecuperacao().isBlank()) {
            throw new IllegalArgumentException("Token de recuperação obrigatório.");
        }
        if (dto.getNovaSenha() == null || dto.getNovaSenha().length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter no mínimo 6 caracteres.");
        }

        String emailExtraido = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(dto.getTokenRecuperacao())
                .getBody()
                .getSubject();

        Usuario usuario = usuarioRepository.findByEmail(emailExtraido)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluirConta(UsuarioUserDetails userDetails) {
        if (userDetails == null || userDetails.getUsuario() == null) {
            throw new SecurityException("Usuário não autenticado.");
        }
        Usuario usuario = usuarioRepository.findById(userDetails.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        usuarioRepository.delete(usuario);
    }
}

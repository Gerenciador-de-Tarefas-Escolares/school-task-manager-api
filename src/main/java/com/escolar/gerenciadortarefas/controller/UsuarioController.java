package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.GoogleTokenDTO;
import com.escolar.gerenciadortarefas.dto.LoginDTO;
import com.escolar.gerenciadortarefas.dto.RegistroDTO;
import com.escolar.gerenciadortarefas.dto.TokenResponseDTO;
import com.escolar.gerenciadortarefas.service.AutentificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints públicos para login tradicional, registro e autenticação via Google")
public class UsuarioController {

    private final AutentificacaoService autentificacaoService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "Login tradicional", description = "Autentica um usuário com e-mail e senha e retorna um token JWT")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(autentificacaoService.loginTradicional(loginDTO));
    }

    @Operation(summary = "Registro de usuário", description = "Cadastra um novo usuário no sistema e retorna um token JWT")
    @PostMapping("/registro")
    public ResponseEntity<TokenResponseDTO> registro(@RequestBody RegistroDTO registroDTO) {
        return ResponseEntity.ok(autentificacaoService.registrarUsuario(registroDTO));
    }

    @Operation(summary = "Login com Google", description = "Valida o idToken do Google OAuth2 e retorna um token JWT da aplicação")
    @PostMapping("/google")
    public ResponseEntity<TokenResponseDTO> loginGoogleReact(@RequestBody GoogleTokenDTO tokenDTO) {
        if (tokenDTO == null || tokenDTO.getIdToken() == null || tokenDTO.getIdToken().trim().isEmpty()) {
            System.err.println("[Google Auth Erro]: O idToken enviado pelo Frontend chegou nulo ou vazio.");
            return ResponseEntity.status(401).build();
        }

        String servidorGoogle = "oauth2." + "googleapis.com";
        String rotaValidacao = "/tokeninfo" + "?id_token=";
        String urlVerificacao = "https://" + servidorGoogle + rotaValidacao + tokenDTO.getIdToken();

        try {
            System.out.println("[Google Auth]: Iniciando handshake de validação com o endpoint do Google...");

            @SuppressWarnings("unchecked")
            Map<String, Object> googleResponse = restTemplate.getForObject(urlVerificacao, Map.class);

            if (googleResponse != null && googleResponse.containsKey("email")) {
                String email = (String) googleResponse.get("email");

                String nome = (String) googleResponse.get("name");
                if (nome == null) {
                    nome = (String) googleResponse.get("given_name");
                }
                if (nome == null) {
                    nome = "Usuário Google";
                }

                System.out.println("[Google Auth Sucesso]: Token legítimo para o e-mail: " + email);
                TokenResponseDTO tokenResponse = autentificacaoService.loginGoogleReact(email, nome);
                return ResponseEntity.ok(tokenResponse);
            }

            System.err.println("[Google Auth Erro]: Resposta do servidor do Google veio vazia ou sem e-mail válido.");
            return ResponseEntity.status(401).build();

        } catch (Exception e) {
            System.err.println("[FAIL - Google Auth Handshake]: Falha crítica na comunicação com a API do Google.");
            System.err.println("[Motivo Técnico]: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
}

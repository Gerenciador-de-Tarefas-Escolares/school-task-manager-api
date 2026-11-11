package com.escolar.gerenciadortarefas.security;

import com.escolar.gerenciadortarefas.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Primary
public class SecureJwtProxyService extends JwtService {

    private static final Logger logger = LoggerFactory.getLogger(SecureJwtProxyService.class);
    private final Set<String> tokensRevogados = ConcurrentHashMap.newKeySet();

    public SecureJwtProxyService() {
        super();
    }

    public void revogarToken(String token) {
        if (token != null && !token.isBlank()) {
            tokensRevogados.add(token);
            logger.info("Token revogado e adicionado à blacklist.");
        }
    }

    @Override
    public Boolean validarToken(String token, UserDetails userDetails) {
        if (token == null || tokensRevogados.contains(token)) {
            logger.warn("Tentativa de uso de token nulo ou revogado.");
            return false;
        }
        return super.validarToken(token, userDetails);
    }
}

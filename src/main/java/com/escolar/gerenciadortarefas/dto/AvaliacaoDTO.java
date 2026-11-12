package com.escolar.gerenciadortarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AvaliacaoDTO(
        @NotBlank(message = "O título da avaliação é obrigatório.")
        String titulo,

        String descricao,

        @NotBlank(message = "O tipo da avaliação é obrigatório (PROVA, TRABALHO, SEMINARIO).")
        String tipo,

        @NotNull(message = "A data da avaliação é obrigatória.")
        LocalDateTime dataAvaliacao,

        BigDecimal nota,
        BigDecimal notaMaxima,
        Long materiaId,
        String novaMateriaNome
) {}

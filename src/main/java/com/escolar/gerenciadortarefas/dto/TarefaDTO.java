package com.escolar.gerenciadortarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TarefaDTO {

    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    private Long materiaId;

    private String materia;
    private Boolean concluida;

    @NotNull(message = "A data de entrega é obrigatória" )
    private LocalDateTime dataEntrega;

    private String prioridade;
    private String status;
}

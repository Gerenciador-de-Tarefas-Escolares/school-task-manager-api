package com.escolar.gerenciadortarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TarefaDTO {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A matéria é obrigatória")
    private String materia;

    @NotNull(message = "A data de entrega é obrigatória" )
    private LocalDate dataEntrega;

    private Boolean concluida;
}

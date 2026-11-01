package com.escolar.tarefasgerenciador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TarefaDTO {
    private String titulo;
    private String materia;
    private LocalDate dataEntrega;
    private Boolean concluida;
}

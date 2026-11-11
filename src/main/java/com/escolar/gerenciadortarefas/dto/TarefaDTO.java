package com.escolar.gerenciadortarefas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {
    private String titulo;
    private String descricao;
    private LocalDateTime dataEntrega;
    private String prioridade;
    private String status;
    private Long materiaId;
    private String novaMateriaNome;
}

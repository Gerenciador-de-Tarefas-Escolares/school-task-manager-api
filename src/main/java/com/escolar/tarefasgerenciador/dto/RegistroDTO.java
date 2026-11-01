package com.escolar.tarefasgerenciador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistroDTO {
    private String nome;
    private String email;
    private String senha;
}

package com.escolar.tarefasgerenciador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDTO {
    private String email;
    private String senha;
}

package com.escolar.gerenciadortarefas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecuperarSenhaDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String tokenRecuperacao;

    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
    private String novaSenha;
}

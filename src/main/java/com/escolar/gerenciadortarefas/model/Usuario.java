package com.escolar.gerenciadortarefas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cadastro")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cadastro_seq")
    @SequenceGenerator(name = "cadastro_seq", sequenceName = "cadastro_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Column(nullable = false, unique = true)
    @Email(message = "E-mail inválido")
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefas;
}

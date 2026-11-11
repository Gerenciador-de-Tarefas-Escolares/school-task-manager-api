package com.escolar.gerenciadortarefas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarefa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tarefa_seq")
    @SequenceGenerator(name = "tarefa_seq", sequenceName = "tarefa_seq", allocationSize = 50)
    private Long id;

    @NotBlank(message = "O título da tarefa é obrigatório")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;

    @Column(nullable = false, length = 15)
    private String status = "PENDENTE";

    @Column(nullable = false, length = 10)
    private String prioridade = "MEDIA";

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastro_id", nullable = false)
    private Usuario usuario;

    @Transient
    public Boolean getConcluida() {
        return "CONCLUIDA".equalsIgnoreCase(this.status);
    }

    public void setConcluida(Boolean concluida) {
        if (concluida != null && concluida) {
            this.status = "CONCLUIDA";
        } else if ("CONCLUIDA".equalsIgnoreCase(this.status)) {
            this.status = "PENDENTE";
        }
    }
}

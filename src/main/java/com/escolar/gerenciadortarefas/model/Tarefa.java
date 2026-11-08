package com.escolar.gerenciadortarefas.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefa")
@Getter
@Setter
@NoArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tarefa_seq")
    @SequenceGenerator(name = "tarefa_seq", sequenceName = "tarefa_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastro_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "materia_id")
    private Long materiaId;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;

    @Column(nullable = false)
    private String prioridade = "MEDIA";

    @Column(nullable = false)
    private String status = "PENDENTE";

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

}

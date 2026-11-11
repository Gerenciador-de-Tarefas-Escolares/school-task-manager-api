package com.escolar.gerenciadortarefas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "materia")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materia_seq")
    @SequenceGenerator(name = "materia_seq", sequenceName = "materia_seq", allocationSize = 50)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastro_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 150)
    private String professor;

    @Column(length = 7)
    private String cor;

    @JsonIgnore
    @OneToMany(mappedBy = "materia")
    private List<Tarefa> tarefas;
}

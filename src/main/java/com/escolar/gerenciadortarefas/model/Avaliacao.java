package com.escolar.gerenciadortarefas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Transient
    private String descricao;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    @Column(precision = 5, scale = 2)
    private BigDecimal nota;

    @Transient
    @Builder.Default
    private BigDecimal notaMaxima = new BigDecimal("10.00");

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastro_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void gerarIdAntesDeSalvar() {
        if (this.id == null) {
            this.id = System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1, 1000);
        }
    }
}

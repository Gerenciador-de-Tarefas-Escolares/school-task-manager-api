package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.AvaliacaoDTO;
import com.escolar.gerenciadortarefas.model.Avaliacao;
import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.AvaliacaoRepository;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private static final Set<String> TIPOS_VALIDOS_BANCO = Set.of("PROVA", "TESTE", "EXAME", "OUTRO");

    private final AvaliacaoRepository avaliacaoRepository;
    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Avaliacao> listarPorUsuario(Long usuarioId) {
        return avaliacaoRepository.findByUsuarioIdOrderByDataAvaliacaoAsc(usuarioId);
    }

    @Transactional
    public Avaliacao criar(AvaliacaoDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Materia materia = resolverMateria(dto.materiaId(), dto.novaMateriaNome(), usuario);

        Avaliacao avaliacao = Avaliacao.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .tipo(normalizarTipo(dto.tipo()))
                .dataAvaliacao(dto.dataAvaliacao())
                .nota(dto.nota())
                .notaMaxima(dto.notaMaxima() != null ? dto.notaMaxima() : new BigDecimal("10.00"))
                .materia(materia)
                .usuario(usuario)
                .build();

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public Avaliacao atualizar(Long id, AvaliacaoDTO dto, Long usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));

        Usuario usuario = avaliacao.getUsuario();
        Materia materia = resolverMateria(dto.materiaId(), dto.novaMateriaNome(), usuario);

        avaliacao.setTitulo(dto.titulo());
        avaliacao.setDescricao(dto.descricao());
        avaliacao.setTipo(normalizarTipo(dto.tipo()));
        avaliacao.setDataAvaliacao(dto.dataAvaliacao());
        avaliacao.setNota(dto.nota());
        if (dto.notaMaxima() != null) {
            avaliacao.setNotaMaxima(dto.notaMaxima());
        }
        avaliacao.setMateria(materia);

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));
        avaliacaoRepository.delete(avaliacao);
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "PROVA";
        }
        String upper = tipo.trim().toUpperCase();
        return TIPOS_VALIDOS_BANCO.contains(upper) ? upper : "OUTRO";
    }

    private Materia resolverMateria(Long materiaId, String novaMateriaNome, Usuario usuario) {
        if (novaMateriaNome != null && !novaMateriaNome.isBlank()) {
            Materia nova = new Materia();
            nova.setNome(novaMateriaNome.trim());
            nova.setCor("#8b5cf6");
            nova.setUsuario(usuario);
            return materiaRepository.save(nova);
        }
        if (materiaId != null) {
            return materiaRepository.findByIdAndUsuarioId(materiaId, usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Matéria escolar não encontrada no banco de dados."));
        }
        return materiaRepository.findByUsuarioId(usuario.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Materia geral = new Materia();
                    geral.setNome("Geral");
                    geral.setCor("#8b5cf6");
                    geral.setUsuario(usuario);
                    return materiaRepository.save(geral);
                });
    }
}

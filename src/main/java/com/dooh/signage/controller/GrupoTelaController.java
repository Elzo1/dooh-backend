package com.dooh.signage.controller;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.GrupoCampanhaResponse;
import com.dooh.signage.dto.GrupoCampanhasVinculoRequest;
import com.dooh.signage.dto.GrupoTelaRequest;
import com.dooh.signage.dto.GrupoTelaResponse;
import com.dooh.signage.dto.GrupoTelasVinculoRequest;
import com.dooh.signage.dto.TelaResumoResponse;
import com.dooh.signage.model.Campanha;
import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.GrupoTela;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.CampanhaRepository;
import com.dooh.signage.repository.EmpresaRepository;
import com.dooh.signage.repository.GrupoTelaRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grupos-telas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GrupoTelaController {

    private final GrupoTelaRepository grupoTelaRepository;
    private final EmpresaRepository empresaRepository;
    private final TelaRepository telaRepository;
    private final CampanhaRepository campanhaRepository;

    @GetMapping
    public List<GrupoTelaResponse> listar() {
        return grupoTelaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public GrupoTelaResponse criar(@RequestBody GrupoTelaRequest request) {
        GrupoTela grupo = GrupoTela.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .empresa(buscarEmpresa(request.getEmpresaId()))
                .ativo(request.getAtivo() != null ? request.getAtivo() : true)
                .build();

        return toResponse(grupoTelaRepository.save(grupo));
    }

    @PutMapping("/{id}")
    public GrupoTelaResponse atualizar(
            @PathVariable Long id,
            @RequestBody GrupoTelaRequest request
    ) {
        GrupoTela grupo = buscarGrupo(id);

        grupo.setNome(request.getNome());
        grupo.setDescricao(request.getDescricao());
        grupo.setEmpresa(buscarEmpresa(request.getEmpresaId()));

        if (request.getAtivo() != null) {
            grupo.setAtivo(request.getAtivo());
        }

        return toResponse(grupoTelaRepository.save(grupo));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        GrupoTela grupo = buscarGrupo(id);

        grupo.setAtivo(false);
        grupoTelaRepository.save(grupo);
    }

    @GetMapping("/{id}/telas")
    public List<TelaResumoResponse> listarTelasDoGrupo(@PathVariable Long id) {
        GrupoTela grupo = buscarGrupo(id);

        return telaRepository.findByGrupo(grupo)
                .stream()
                .map(this::toTelaResponse)
                .toList();
    }

    @GetMapping("/{id}/telas-disponiveis")
    public List<TelaResumoResponse> listarTelasDisponiveisParaGrupo(@PathVariable Long id) {
        GrupoTela grupo = buscarGrupo(id);

        return telaRepository.findAll()
                .stream()
                .filter(tela ->
                        tela.getGrupo() == null ||
                        tela.getGrupo().getId().equals(grupo.getId())
                )
                .map(this::toTelaResponse)
                .toList();
    }

    @PutMapping("/{id}/telas")
    @Transactional
    public GrupoTelaResponse atualizarTelasDoGrupo(
            @PathVariable Long id,
            @RequestBody GrupoTelasVinculoRequest request
    ) {
        GrupoTela grupo = buscarGrupo(id);
        List<Long> telaIds = request.getTelaIds() != null ? request.getTelaIds() : List.of();

        List<Tela> telasAtuaisDoGrupo = telaRepository.findByGrupo(grupo);

        for (Tela tela : telasAtuaisDoGrupo) {
            if (!telaIds.contains(tela.getId())) {
                tela.setGrupo(null);
                telaRepository.save(tela);
            }
        }

        for (Long telaId : telaIds) {
            Tela tela = telaRepository.findById(telaId)
                    .orElseThrow(() -> new IllegalArgumentException("Tela não encontrada: " + telaId));

            tela.setGrupo(grupo);

            if (tela.getEmpresa() == null && grupo.getEmpresa() != null) {
                tela.setEmpresa(grupo.getEmpresa());
            }

            telaRepository.save(tela);
        }

        return toResponse(grupoTelaRepository.save(grupo));
    }

    @GetMapping("/{id}/campanhas")
    public List<GrupoCampanhaResponse> listarCampanhasDoGrupo(@PathVariable Long id) {
        GrupoTela grupo = buscarGrupo(id);

        return grupo.getCampanhas()
                .stream()
                .map(campanha -> toGrupoCampanhaResponse(campanha, true))
                .toList();
    }

    @GetMapping("/{id}/campanhas-disponiveis")
    public List<GrupoCampanhaResponse> listarCampanhasDisponiveisDoGrupo(@PathVariable Long id) {
        GrupoTela grupo = buscarGrupo(id);

        Set<Long> campanhasSelecionadasIds = grupo.getCampanhas()
                .stream()
                .map(Campanha::getId)
                .collect(Collectors.toSet());

        return campanhaRepository.findAll()
                .stream()
                .filter(campanha ->
                        campanha.getEmpresa() == null ||
                        grupo.getEmpresa() == null ||
                        campanha.getEmpresa().getId().equals(grupo.getEmpresa().getId())
                )
                .map(campanha -> toGrupoCampanhaResponse(
                        campanha,
                        campanhasSelecionadasIds.contains(campanha.getId())
                ))
                .toList();
    }

    @PutMapping("/{id}/campanhas")
    @Transactional
    public GrupoTelaResponse atualizarCampanhasDoGrupo(
            @PathVariable Long id,
            @RequestBody GrupoCampanhasVinculoRequest request
    ) {
        GrupoTela grupo = buscarGrupo(id);
        List<Long> campanhaIds = request.getCampanhaIds() != null ? request.getCampanhaIds() : List.of();

        Set<Campanha> novasCampanhas = new LinkedHashSet<>();

        for (Long campanhaId : campanhaIds) {
            Campanha campanha = campanhaRepository.findById(campanhaId)
                    .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada: " + campanhaId));

            if (grupo.getEmpresa() != null
                    && campanha.getEmpresa() != null
                    && !campanha.getEmpresa().getId().equals(grupo.getEmpresa().getId())) {
                throw new IllegalArgumentException(
                        "A campanha " + campanha.getNome() + " pertence a outra empresa."
                );
            }

            novasCampanhas.add(campanha);
        }

        grupo.getCampanhas().clear();
        grupo.getCampanhas().addAll(novasCampanhas);

        return toResponse(grupoTelaRepository.save(grupo));
    }

    private GrupoTela buscarGrupo(Long id) {
        return grupoTelaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));
    }

    private Empresa buscarEmpresa(Long empresaId) {
        if (empresaId == null) {
            return null;
        }

        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
    }

    private GrupoTelaResponse toResponse(GrupoTela grupo) {
        return GrupoTelaResponse.builder()
                .id(grupo.getId())
                .nome(grupo.getNome())
                .descricao(grupo.getDescricao())
                .empresaId(grupo.getEmpresa() != null ? grupo.getEmpresa().getId() : null)
                .empresaNome(grupo.getEmpresa() != null ? grupo.getEmpresa().getNomeFantasia() : null)
                .ativo(grupo.getAtivo())
                .totalTelas(telaRepository.countByGrupo(grupo))
                .totalCampanhas(grupo.getCampanhas() != null ? (long) grupo.getCampanhas().size() : 0L)
                .build();
    }

    private TelaResumoResponse toTelaResponse(Tela tela) {
        return TelaResumoResponse.builder()
                .id(tela.getId())
                .nome(tela.getNome())
                .codigoUnico(tela.getCodigoUnico())
                .local(tela.getLocal())
                .cidade(tela.getCidade())
                .endereco(tela.getEndereco())
                .orientacao(tela.getOrientacao() != null ? tela.getOrientacao().name() : null)
                .polegadas(tela.getPolegadas())
                .resolucao(tela.getResolucao())
                .status(tela.getStatus())
                .ip(tela.getIp())
                .ultimaConexao(tela.getUltimaConexao())
                .observacoes(tela.getObservacoes())
                .telemetria(tela.getTelemetria())
                .grupoId(tela.getGrupo() != null ? tela.getGrupo().getId() : null)
                .grupoNome(tela.getGrupo() != null ? tela.getGrupo().getNome() : null)
                .empresaId(tela.getEmpresa() != null ? tela.getEmpresa().getId() : null)
                .empresaNome(tela.getEmpresa() != null ? tela.getEmpresa().getNomeFantasia() : null)
                .build();
    }

    private GrupoCampanhaResponse toGrupoCampanhaResponse(
            Campanha campanha,
            Boolean selecionada
    ) {
        return GrupoCampanhaResponse.builder()
                .id(campanha.getId())
                .nome(campanha.getNome())
                .empresaId(campanha.getEmpresa() != null ? campanha.getEmpresa().getId() : null)
                .empresaNome(campanha.getEmpresa() != null ? campanha.getEmpresa().getNomeFantasia() : null)
                .urlMidia(campanha.getUrlMidia())
                .tempoExibicao(campanha.getTempoExibicao())
                .prioridade(campanha.getPrioridade())
                .status(campanha.getStatus())
                .selecionada(selecionada)
                .build();
    }
}
package com.dooh.signage.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.TelaRequest;
import com.dooh.signage.dto.TelaResumoResponse;
import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.GrupoTela;
import com.dooh.signage.model.Orientacao;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.EmpresaRepository;
import com.dooh.signage.repository.GrupoTelaRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/telas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelaController {

    private final TelaRepository telaRepository;
    private final GrupoTelaRepository grupoTelaRepository;
    private final EmpresaRepository empresaRepository;

    @GetMapping
    public List<TelaResumoResponse> listar() {
        return telaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public TelaResumoResponse criar(@RequestBody TelaRequest request) {
        GrupoTela grupo = buscarGrupo(request.getGrupoId());
        Empresa empresa = buscarEmpresa(request.getEmpresaId());

        if (empresa == null && grupo != null) {
            empresa = grupo.getEmpresa();
        }

        String codigoCurto = gerarOuNormalizarCodigoCurto(request.getCodigoCurto(), null);

        Tela tela = Tela.builder()
                .nome(textoObrigatorio(request.getNome(), "Nome da tela é obrigatório."))
                .codigoUnico(UUID.randomUUID().toString())
                .codigoCurto(codigoCurto)
                .local(textoOuNull(request.getLocal()))
                .cidade(textoOuNull(request.getCidade()))
                .endereco(textoOuNull(request.getEndereco()))
                .orientacao(resolverOrientacao(request.getOrientacao()))
                .polegadas(textoOuPadrao(request.getPolegadas(), "LIVRE"))
                .resolucao(textoOuPadrao(request.getResolucao(), "1920x1080"))
                .empresa(empresa)
                .grupo(grupo)
                .status("OFFLINE")
                .observacoes(textoOuNull(request.getObservacoes()))
                .build();

        return toResponse(telaRepository.save(tela));
    }

    @PutMapping("/{id}")
    public TelaResumoResponse atualizar(
            @PathVariable Long id,
            @RequestBody TelaRequest request
    ) {
        Tela tela = telaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tela não encontrada."));

        GrupoTela grupo = buscarGrupo(request.getGrupoId());
        Empresa empresa = buscarEmpresa(request.getEmpresaId());

        if (empresa == null && grupo != null) {
            empresa = grupo.getEmpresa();
        }

        String codigoCurto = gerarOuNormalizarCodigoCurto(request.getCodigoCurto(), tela.getId());

        tela.setNome(textoObrigatorio(request.getNome(), "Nome da tela é obrigatório."));
        tela.setCodigoCurto(codigoCurto);
        tela.setLocal(textoOuNull(request.getLocal()));
        tela.setCidade(textoOuNull(request.getCidade()));
        tela.setEndereco(textoOuNull(request.getEndereco()));
        tela.setOrientacao(resolverOrientacao(request.getOrientacao()));
        tela.setPolegadas(textoOuPadrao(request.getPolegadas(), "LIVRE"));
        tela.setResolucao(textoOuPadrao(request.getResolucao(), "1920x1080"));
        tela.setObservacoes(textoOuNull(request.getObservacoes()));
        tela.setGrupo(grupo);
        tela.setEmpresa(empresa);

        return toResponse(telaRepository.save(tela));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        telaRepository.deleteById(id);
    }

    private TelaResumoResponse toResponse(Tela tela) {
        String codigoPlayer = tela.getCodigoCurto() != null && !tela.getCodigoCurto().isBlank()
                ? tela.getCodigoCurto()
                : tela.getCodigoUnico();

        return TelaResumoResponse.builder()
                .id(tela.getId())
                .nome(tela.getNome())
                .codigoUnico(tela.getCodigoUnico())
                .codigoCurto(tela.getCodigoCurto())
                .linkPlayer("/player/" + codigoPlayer)
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

    private GrupoTela buscarGrupo(Long grupoId) {
        if (grupoId == null) return null;

        return grupoTelaRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo de telas não encontrado."));
    }

    private Empresa buscarEmpresa(Long empresaId) {
        if (empresaId == null) return null;

        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
    }

    private Orientacao resolverOrientacao(String orientacao) {
        if (orientacao == null || orientacao.isBlank()) {
            return Orientacao.HORIZONTAL;
        }

        if ("VERTICAL".equalsIgnoreCase(orientacao)) {
            return Orientacao.VERTICAL;
        }

        return Orientacao.HORIZONTAL;
    }

    private String gerarOuNormalizarCodigoCurto(String codigoInformado, Long telaIdAtual) {
        String codigo = textoOuNull(codigoInformado);

        if (codigo == null) {
            return null;
        }

        codigo = codigo
                .trim()
                .toUpperCase()
                .replace(" ", "-");

        if (!codigo.matches("^[A-Z0-9_-]{3,30}$")) {
            throw new IllegalArgumentException("Código curto deve ter 3 a 30 caracteres, usando apenas letras, números, _ ou -.");
        }

        telaRepository.findByCodigoCurtoIgnoreCase(codigo).ifPresent(telaExistente -> {
            if (telaIdAtual == null || !telaExistente.getId().equals(telaIdAtual)) {
                throw new IllegalArgumentException("Já existe uma tela com este código curto.");
            }
        });

        return codigo;
    }

    private String textoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        return valor.trim();
    }

    private String textoOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }

        return valor.trim();
    }

    private String textoOuNull(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}
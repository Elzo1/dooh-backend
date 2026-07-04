package com.dooh.signage.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.dto.CampanhaRequest;
import com.dooh.signage.dto.CampanhaResumoResponse;
import com.dooh.signage.model.Campanha;
import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.Midia;
import com.dooh.signage.repository.CampanhaRepository;
import com.dooh.signage.repository.EmpresaRepository;
import com.dooh.signage.repository.MidiaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private static final String STATUS_ATIVA = "ATIVA";
    private static final String STATUS_PAUSADA = "PAUSADA";
    private static final String DIAS_TODOS = "1,2,3,4,5,6,7";

    private final CampanhaRepository campanhaRepository;
    private final EmpresaRepository empresaRepository;
    private final MidiaRepository midiaRepository;

    @Transactional(readOnly = true)
    public List<CampanhaResumoResponse> listar() {
        return campanhaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CampanhaResumoResponse buscarPorId(Long id) {
        return toResponse(buscarCampanha(id));
    }

    @Transactional
    public CampanhaResumoResponse criar(CampanhaRequest request) {
        Campanha campanha = new Campanha();

        preencherCampanha(campanha, request);

        return toResponse(campanhaRepository.save(campanha));
    }

    @Transactional
    public CampanhaResumoResponse atualizar(Long id, CampanhaRequest request) {
        Campanha campanha = buscarCampanha(id);

        preencherCampanha(campanha, request);

        return toResponse(campanhaRepository.save(campanha));
    }

    @Transactional
    public CampanhaResumoResponse alternarStatus(Long id) {
        Campanha campanha = buscarCampanha(id);

        String statusAtual = normalizarStatus(campanha.getStatus());

        if (STATUS_ATIVA.equals(statusAtual)) {
            campanha.setStatus(STATUS_PAUSADA);
        } else {
            campanha.setStatus(STATUS_ATIVA);
        }

        return toResponse(campanhaRepository.save(campanha));
    }

    @Transactional
    public void excluir(Long id) {
        Campanha campanha = buscarCampanha(id);
        campanhaRepository.delete(campanha);
    }

    private void preencherCampanha(Campanha campanha, CampanhaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da campanha são obrigatórios.");
        }

        Empresa empresa = buscarEmpresa(request.getEmpresaId());
        Midia midia = buscarMidia(request.getMidiaId());

        campanha.setNome(textoObrigatorio(request.getNome(), "Nome da campanha é obrigatório."));
        campanha.setEmpresa(empresa);
        campanha.setUrlMidia(midia.getUrl());
        campanha.setDataInicio(request.getDataInicio() != null ? request.getDataInicio() : LocalDate.now());
        campanha.setDataFim(request.getDataFim() != null ? request.getDataFim() : LocalDate.now().plusYears(1));
        campanha.setDiasSemana(textoOuPadrao(request.getDiasSemana(), DIAS_TODOS));
        campanha.setHoraInicio(request.getHoraInicio() != null ? request.getHoraInicio() : LocalTime.MIN);
        campanha.setHoraFim(request.getHoraFim() != null ? request.getHoraFim() : LocalTime.of(23, 59, 59));
        campanha.setTempoExibicao(request.getTempoExibicao() != null ? request.getTempoExibicao() : 15);
        campanha.setPrioridade(request.getPrioridade() != null ? request.getPrioridade() : 1);
        campanha.setStatus(normalizarStatus(textoOuPadrao(request.getStatus(), STATUS_ATIVA)));
    }

    private Campanha buscarCampanha(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da campanha é obrigatório.");
        }

        return campanhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));
    }

    private Empresa buscarEmpresa(Long empresaId) {
        if (empresaId == null) {
            throw new IllegalArgumentException("Empresa é obrigatória.");
        }

        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
    }

    private Midia buscarMidia(Long midiaId) {
        if (midiaId == null) {
            throw new IllegalArgumentException("Mídia é obrigatória.");
        }

        return midiaRepository.findById(midiaId)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada."));
    }

    private CampanhaResumoResponse toResponse(Campanha campanha) {
        return CampanhaResumoResponse.builder()
                .id(campanha.getId())
                .nome(campanha.getNome())
                .empresaId(campanha.getEmpresa() != null ? campanha.getEmpresa().getId() : null)
                .empresaNome(campanha.getEmpresa() != null ? campanha.getEmpresa().getNomeFantasia() : null)
                .urlMidia(campanha.getUrlMidia())
                .dataInicio(campanha.getDataInicio())
                .dataFim(campanha.getDataFim())
                .diasSemana(campanha.getDiasSemana())
                .horaInicio(campanha.getHoraInicio())
                .horaFim(campanha.getHoraFim())
                .tempoExibicao(campanha.getTempoExibicao())
                .prioridade(campanha.getPrioridade())
                .status(campanha.getStatus())
                .build();
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

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) {
            return STATUS_ATIVA;
        }

        return status.trim().toUpperCase();
    }
}
package com.dooh.signage.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.dto.EmpresaDashboardResponse;
import com.dooh.signage.dto.EmpresaRequest;
import com.dooh.signage.dto.EmpresaResponse;
import com.dooh.signage.dto.EmpresaResumoResponse;
import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.CampanhaRepository;
import com.dooh.signage.repository.EmpresaRepository;
import com.dooh.signage.repository.GrupoTelaRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final TelaRepository telaRepository;
    private final GrupoTelaRepository grupoTelaRepository;
    private final CampanhaRepository campanhaRepository;

    public List<EmpresaResponse> listar(String busca, String status) {
        List<Empresa> empresas;

        if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();

            empresas = empresaRepository
                    .findByNomeFantasiaContainingIgnoreCaseOrRazaoSocialContainingIgnoreCaseOrCnpjContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByNomeFantasiaAsc(
                            termo,
                            termo,
                            termo,
                            termo
                    );
        } else if (status != null && !status.isBlank() && !"TODAS".equalsIgnoreCase(status)) {
            empresas = empresaRepository.findByStatusOrderByNomeFantasiaAsc(status.toUpperCase());
        } else {
            empresas = empresaRepository.findAll()
                    .stream()
                    .sorted((a, b) -> texto(a.getNomeFantasia()).compareToIgnoreCase(texto(b.getNomeFantasia())))
                    .toList();
        }

        return empresas.stream()
                .map(this::toResponse)
                .toList();
    }

    public EmpresaResponse buscarPorId(Long id) {
        Empresa empresa = buscarEmpresa(id);
        return toResponse(empresa);
    }

    @Transactional
    public EmpresaResponse criar(EmpresaRequest request) {
        validarObrigatorios(request);

        String email = normalizarEmail(request.getEmail());
        String cnpj = normalizarTexto(request.getCnpj());

        if (empresaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe empresa cadastrada com este e-mail.");
        }

        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException("Já existe empresa cadastrada com este CNPJ.");
        }

        Empresa empresa = Empresa.builder()
                .razaoSocial(normalizarTexto(request.getRazaoSocial()))
                .nomeFantasia(normalizarTexto(request.getNomeFantasia()))
                .responsavel(normalizarTexto(request.getResponsavel()))
                .telefone(normalizarTexto(request.getTelefone()))
                .whatsapp(normalizarTexto(request.getWhatsapp()))
                .email(email)
                .endereco(normalizarTexto(request.getEndereco()))
                .cidade(normalizarTexto(request.getCidade()))
                .estado(normalizarEstado(request.getEstado()))
                .cnpj(cnpj)
                .urlLogo(normalizarTexto(request.getUrlLogo()))
                .status(normalizarStatus(request.getStatus()))
                .observacoes(normalizarTexto(request.getObservacoes()))
                .build();

        return toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        validarObrigatorios(request);

        Empresa empresa = buscarEmpresa(id);

        String email = normalizarEmail(request.getEmail());
        String cnpj = normalizarTexto(request.getCnpj());

        empresaRepository.findByEmail(email).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe empresa cadastrada com este e-mail.");
            }
        });

        empresaRepository.findByCnpj(cnpj).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe empresa cadastrada com este CNPJ.");
            }
        });

        empresa.setRazaoSocial(normalizarTexto(request.getRazaoSocial()));
        empresa.setNomeFantasia(normalizarTexto(request.getNomeFantasia()));
        empresa.setResponsavel(normalizarTexto(request.getResponsavel()));
        empresa.setTelefone(normalizarTexto(request.getTelefone()));
        empresa.setWhatsapp(normalizarTexto(request.getWhatsapp()));
        empresa.setEmail(email);
        empresa.setEndereco(normalizarTexto(request.getEndereco()));
        empresa.setCidade(normalizarTexto(request.getCidade()));
        empresa.setEstado(normalizarEstado(request.getEstado()));
        empresa.setCnpj(cnpj);
        empresa.setUrlLogo(normalizarTexto(request.getUrlLogo()));
        empresa.setStatus(normalizarStatus(request.getStatus()));
        empresa.setObservacoes(normalizarTexto(request.getObservacoes()));

        return toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public void desativar(Long id) {
        Empresa empresa = buscarEmpresa(id);
        empresa.setStatus("INATIVO");
        empresaRepository.save(empresa);
    }

    public EmpresaResumoResponse resumo() {
        return EmpresaResumoResponse.builder()
                .totalEmpresas(empresaRepository.count())
                .empresasAtivas(empresaRepository.countByStatus("ATIVO"))
                .empresasInativas(empresaRepository.countByStatus("INATIVO"))
                .totalTelas(telaRepository.count())
                .telasOnline(telaRepository.countByStatus("ONLINE"))
                .telasOffline(telaRepository.countByStatus("OFFLINE"))
                .totalCampanhas(campanhaRepository.count())
                .build();
    }

    private Empresa buscarEmpresa(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
    }

    private EmpresaResponse toResponse(Empresa empresa) {
        return EmpresaResponse.builder()
                .id(empresa.getId())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .responsavel(empresa.getResponsavel())
                .telefone(empresa.getTelefone())
                .whatsapp(empresa.getWhatsapp())
                .email(empresa.getEmail())
                .endereco(empresa.getEndereco())
                .cidade(empresa.getCidade())
                .estado(empresa.getEstado())
                .cnpj(empresa.getCnpj())
                .urlLogo(empresa.getUrlLogo())
                .status(empresa.getStatus())
                .observacoes(empresa.getObservacoes())
                .totalTelas(telaRepository.countByEmpresa(empresa))
                .telasOnline(telaRepository.countByEmpresaAndStatus(empresa, "ONLINE"))
                .telasOffline(telaRepository.countByEmpresaAndStatus(empresa, "OFFLINE"))
                .totalGrupos(grupoTelaRepository.countByEmpresa(empresa))
                .totalCampanhas(campanhaRepository.countByEmpresa(empresa))
                .createdAt(empresa.getCreatedAt())
                .updatedAt(empresa.getUpdatedAt())
                .build();
    }

    private void validarObrigatorios(EmpresaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da empresa são obrigatórios.");
        }

        if (vazio(request.getRazaoSocial())) {
            throw new IllegalArgumentException("Razão social é obrigatória.");
        }

        if (vazio(request.getNomeFantasia())) {
            throw new IllegalArgumentException("Nome fantasia é obrigatório.");
        }

        if (vazio(request.getEmail())) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }

        if (vazio(request.getCnpj())) {
            throw new IllegalArgumentException("CNPJ é obrigatório.");
        }

        if (vazio(request.getEstado())) {
            throw new IllegalArgumentException("Estado é obrigatório.");
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizarTexto(String valor) {
        if (valor == null) return null;
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    private String normalizarEmail(String valor) {
        String texto = normalizarTexto(valor);

        if (texto == null) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }

        return texto.toLowerCase();
    }

    private String normalizarEstado(String valor) {
        String texto = normalizarTexto(valor);

        if (texto == null) {
            throw new IllegalArgumentException("Estado é obrigatório.");
        }

        texto = texto.toUpperCase();

        if (texto.length() != 2) {
            throw new IllegalArgumentException("Estado deve conter 2 letras. Ex: BA.");
        }

        return texto;
    }

    private String normalizarStatus(String valor) {
        if (valor == null || valor.isBlank()) {
            return "ATIVO";
        }

        String status = valor.trim().toUpperCase();

        if (!status.equals("ATIVO") && !status.equals("INATIVO")) {
            return "ATIVO";
        }

        return status;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
    
    
    public EmpresaDashboardResponse dashboard(Long id) {
        Empresa empresa = buscarEmpresa(id);

        Long totalTelas = telaRepository.countByEmpresa(empresa);
        Long telasOnline = telaRepository.countByEmpresaAndStatus(empresa, "ONLINE");
        Long telasOffline = telaRepository.countByEmpresaAndStatus(empresa, "OFFLINE");
        Long totalGrupos = grupoTelaRepository.countByEmpresa(empresa);
        Long totalCampanhas = campanhaRepository.countByEmpresa(empresa);

        Long campanhasAtivas = campanhaRepository.findByEmpresa(empresa)
                .stream()
                .filter(campanha -> "ATIVA".equalsIgnoreCase(campanha.getStatus()))
                .count();

        Double percentualOnline = totalTelas > 0
                ? (telasOnline * 100.0) / totalTelas
                : 0.0;

        ZonedDateTime ultimaConexao = telaRepository.findByEmpresa(empresa)
                .stream()
                .map(Tela::getUltimaConexao)
                .filter(data -> data != null)
                .max(ZonedDateTime::compareTo)
                .orElse(null);

        return EmpresaDashboardResponse.builder()
                .empresa(toResponse(empresa))
                .totalTelas(totalTelas)
                .telasOnline(telasOnline)
                .telasOffline(telasOffline)
                .totalGrupos(totalGrupos)
                .totalCampanhas(totalCampanhas)
                .campanhasAtivas(campanhasAtivas)
                .percentualOnline(percentualOnline)
                .ultimaConexao(ultimaConexao)
                .build();
    }
}
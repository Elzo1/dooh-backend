package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaResponse {

    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String responsavel;
    private String telefone;
    private String whatsapp;
    private String email;
    private String endereco;
    private String cidade;
    private String estado;
    private String cnpj;
    private String urlLogo;
    private String status;
    private String observacoes;

    private Long totalTelas;
    private Long telasOnline;
    private Long telasOffline;
    private Long totalGrupos;
    private Long totalCampanhas;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
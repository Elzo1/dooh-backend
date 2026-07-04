package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrupoTelaResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Long empresaId;
    private String empresaNome;
    private Boolean ativo;
    private Long totalTelas;
    private Long totalCampanhas;
}
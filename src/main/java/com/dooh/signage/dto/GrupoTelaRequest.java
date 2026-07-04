package com.dooh.signage.dto;

import lombok.Data;

@Data
public class GrupoTelaRequest {
    private String nome;
    private String descricao;
    private Long empresaId;
    private Boolean ativo;
}
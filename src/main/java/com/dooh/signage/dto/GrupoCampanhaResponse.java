package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrupoCampanhaResponse {

    private Long id;
    private String nome;
    private Long empresaId;
    private String empresaNome;
    private String urlMidia;
    private Integer tempoExibicao;
    private Integer prioridade;
    private String status;
    private Boolean selecionada;
}
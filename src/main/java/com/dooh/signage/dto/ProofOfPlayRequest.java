package com.dooh.signage.dto;

import lombok.Data;

@Data
public class ProofOfPlayRequest {
    private String telaCodigo;
    private Long campanhaId;
    private String campanhaNome;
    private Long playlistId;

    private String inicioExibicao;
    private String fimExibicao;

    private Integer duracaoProgramada;
    private Integer duracaoReal;

    private String status;
    private String tipoMidia;
    private String urlMidia;

    private String cidade;
    private String temperatura;

    private String versaoPlayer;
}
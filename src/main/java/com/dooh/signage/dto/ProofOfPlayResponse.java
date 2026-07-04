package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfPlayResponse {

    private Long id;

    private Long telaId;
    private String telaCodigo;
    private String telaNome;

    private Long campanhaId;
    private String campanhaNome;

    private Long playlistId;

    private ZonedDateTime inicioExibicao;
    private ZonedDateTime fimExibicao;

    private Integer duracaoProgramada;
    private Integer duracaoReal;

    private String status;
    private String tipoMidia;
    private String urlMidia;

    private String cidade;
    private String temperatura;

    private String versaoPlayer;
    private String ip;

    private ZonedDateTime createdAt;
}

package com.dooh.signage.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TelemetriaRequest {

    private Long espacoLivreMb;

    private Long memoriaUsoMb;

    private BigDecimal cpuUsoPorcento;

    private String resolucao;

    private String orientacao;

    private String versaoPlayer;

    private String campanhaAtual;

    private Long uptimeSegundos;

    private String userAgent;
}
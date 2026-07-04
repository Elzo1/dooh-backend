package com.dooh.signage.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Telemetria {

    @Column(name = "telemetria_espaco_livre_mb")
    private Long espacoLivreMb;

    @Column(name = "telemetria_memoria_uso_mb")
    private Long memoriaUsoMb;

    @Column(name = "telemetria_cpu_uso_porcento", precision = 5, scale = 2)
    private BigDecimal cpuUsoPorcento;

    @Column(name = "telemetria_resolucao")
    private String resolucao;

    @Column(name = "telemetria_orientacao")
    private String orientacao;

    @Column(name = "telemetria_versao_player")
    private String versaoPlayer;

    @Column(name = "telemetria_campanha_atual")
    private String campanhaAtual;

    @Column(name = "telemetria_uptime_segundos")
    private Long uptimeSegundos;

    @Column(name = "telemetria_user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
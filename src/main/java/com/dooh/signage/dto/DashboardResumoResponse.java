package com.dooh.signage.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumoResponse {

    private Long totalTelas;
    private Long telasOnline;
    private Long telasOffline;

    private Long totalCampanhas;
    private Long campanhasAtivas;

    private Long empresas;
    private Long playlists;
    private Long uploads;

    private Long proofOfPlayHoje;
    private Long proofOfPlaySucesso;

    private BigDecimal cpuMedia;
    private Long memoriaMediaMb;
    private Long espacoLivreMedioMb;

    private BigDecimal faturamentoEstimado;

    private List<TelaStatusDto> ultimasTelas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelaStatusDto {
        private Long id;
        private String nome;
        private String status;
        private String ip;
        private String ultimaConexao;
        private String campanhaAtual;
        private String versaoPlayer;
    }
}
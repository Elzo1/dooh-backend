package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaDashboardResponse {

    private EmpresaResponse empresa;

    private Long totalTelas;
    private Long telasOnline;
    private Long telasOffline;
    private Long totalGrupos;
    private Long totalCampanhas;
    private Long campanhasAtivas;

    private Double percentualOnline;
    private ZonedDateTime ultimaConexao;
}
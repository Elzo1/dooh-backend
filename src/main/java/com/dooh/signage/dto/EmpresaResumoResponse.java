package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaResumoResponse {

    private Long totalEmpresas;
    private Long empresasAtivas;
    private Long empresasInativas;
    private Long totalTelas;
    private Long telasOnline;
    private Long telasOffline;
    private Long totalCampanhas;
}
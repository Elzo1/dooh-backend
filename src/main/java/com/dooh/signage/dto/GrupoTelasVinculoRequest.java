package com.dooh.signage.dto;

import java.util.List;

import lombok.Data;

@Data
public class GrupoTelasVinculoRequest {
    private List<Long> telaIds;
}
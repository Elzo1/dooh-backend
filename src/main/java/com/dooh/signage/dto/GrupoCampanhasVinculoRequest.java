package com.dooh.signage.dto;

import java.util.List;

import lombok.Data;

@Data
public class GrupoCampanhasVinculoRequest {
    private List<Long> campanhaIds;
}
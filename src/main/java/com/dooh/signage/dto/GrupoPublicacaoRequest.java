package com.dooh.signage.dto;

import java.util.List;

import lombok.Data;

@Data
public class GrupoPublicacaoRequest {
    private List<Long> campanhaIds;
}
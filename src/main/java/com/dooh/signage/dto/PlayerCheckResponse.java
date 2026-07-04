package com.dooh.signage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCheckResponse {
    private String telaCodigo;
    private Long versaoAtual;
    private Boolean update;
}
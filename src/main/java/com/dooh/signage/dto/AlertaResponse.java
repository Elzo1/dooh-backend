package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertaResponse {
    private Long id;
    private Long telaId;
    private String telaNome;
    private String tipo;
    private String nivel;
    private String mensagem;
    private Boolean lido;
    private Boolean resolvido;
    private ZonedDateTime createdAt;
}
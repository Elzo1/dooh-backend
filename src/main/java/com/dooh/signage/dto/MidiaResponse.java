package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MidiaResponse {
    private Long id;
    private String nomeOriginal;
    private String nomeArquivo;
    private String nomeExibicao;
    private String tipo;
    private String categoria;
    private Long tamanhoBytes;
    private String url;
    private String thumbnailUrl;
    private Integer largura;
    private Integer altura;
    private Long duracaoSegundos;
    private String status;
    private ZonedDateTime createdAt;
}
package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistItemResponse {
    private Long id;
    private Long campanhaId;
    private String campanhaNome;
    private String urlMidia;
    private Integer tempoExibicao;
    private Integer ordem;
    private String status;
}
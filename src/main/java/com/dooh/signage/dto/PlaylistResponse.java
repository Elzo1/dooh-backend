package com.dooh.signage.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {

    private String telaCodigo;
    private String nomeTela;
    private String orientacao;
    private Long versaoPlaylist;
    private List<PlaylistItemDto> itens;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaylistItemDto {
        private Long campanhaId;
        private String nomeCampanha;
        private String urlMidia;
        private Integer tempoExibicao;
        private Integer ordem;
        private Integer prioridade;
        private String diasSemana;
        private String horaInicio;
        private String horaFim;
        private String dataInicio;
        private String dataFim;
    }
}
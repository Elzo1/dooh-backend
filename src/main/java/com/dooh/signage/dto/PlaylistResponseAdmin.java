package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlaylistResponseAdmin {
    private Long id;
    private String nome;
    private Boolean ativa;
    private Long telaId;
    private String telaNome;
    private List<PlaylistItemResponse> itens;
}
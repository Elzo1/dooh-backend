package com.dooh.signage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerUpdateMessage {
    private String tipo;
    private Long telaId;
    private String mensagem;
}
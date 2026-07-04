package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DispositivoAtivacaoResponse {

    private Long id;
    private String deviceUuid;
    private String pin;
    private String status;
    private Boolean ativado;
    private ZonedDateTime expiraEm;

    private Long telaId;
    private String telaNome;
    private String codigoUnico;
    private String codigoCurto;
    private String linkPlayer;
}
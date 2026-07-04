package com.dooh.signage.dto;

import lombok.Data;

@Data
public class VincularDispositivoRequest {

    private String pin;
    private Long telaId;
}
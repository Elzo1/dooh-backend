package com.dooh.signage.dto;

import lombok.Data;

@Data
public class SolicitarPinRequest {

    private String deviceUuid;
    private String versaoPlayer;
}
package com.dooh.signage.dto;

import lombok.Data;

@Data
public class DispositivoPingRequest {

    private String deviceUuid;
    private String versaoPlayer;
}
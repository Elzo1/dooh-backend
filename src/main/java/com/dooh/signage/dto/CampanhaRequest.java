package com.dooh.signage.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class CampanhaRequest {

    private String nome;
    private Long midiaId;
    private Long empresaId;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Exemplo: "1,2,3,4,5" ou "1,2,3,4,5,6,7"
    private String diasSemana;

    private LocalTime horaInicio;
    private LocalTime horaFim;

    private Integer tempoExibicao;
    private Integer prioridade;

    // ATIVA, INATIVA
    private String status;
}
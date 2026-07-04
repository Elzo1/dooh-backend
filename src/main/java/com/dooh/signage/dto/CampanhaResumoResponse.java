package com.dooh.signage.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampanhaResumoResponse {
    private Long id;
    private String nome;
    private Long empresaId;
    private String empresaNome;
    private String urlMidia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer tempoExibicao;
    private Integer prioridade;
    private String status;
}
package com.dooh.signage.dto;

import java.time.ZonedDateTime;

import com.dooh.signage.model.Telemetria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelaResumoResponse {

    private Long id;
    private String nome;
    private String codigoUnico;
    private String codigoCurto;
    private String linkPlayer;
    private String local;
    private String cidade;
    private String endereco;
    private String orientacao;
    private String polegadas;
    private String resolucao;
    private String status;
    private String ip;
    private ZonedDateTime ultimaConexao;
    private String observacoes;
    private Telemetria telemetria;

    private Long grupoId;
    private String grupoNome;

    private Long empresaId;
    private String empresaNome;
}
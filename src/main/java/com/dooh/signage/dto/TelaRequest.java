package com.dooh.signage.dto;

import lombok.Data;

@Data
public class TelaRequest {

    private String nome;
    private String codigoCurto;
    private String local;
    private String cidade;
    private String endereco;
    private String orientacao;
    private String polegadas;
    private String resolucao;
    private String observacoes;

    private Long grupoId;
    private Long empresaId;
}
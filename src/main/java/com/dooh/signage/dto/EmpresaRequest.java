package com.dooh.signage.dto;

import lombok.Data;

@Data
public class EmpresaRequest {

    private String razaoSocial;
    private String nomeFantasia;
    private String responsavel;
    private String telefone;
    private String whatsapp;
    private String email;
    private String endereco;
    private String cidade;
    private String estado;
    private String cnpj;
    private String urlLogo;
    private String status;
    private String observacoes;
}
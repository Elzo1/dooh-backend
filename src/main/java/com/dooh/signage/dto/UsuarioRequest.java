package com.dooh.signage.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String nome;
    private String username;
    private String email;
    private String senha;
    private String role;
}
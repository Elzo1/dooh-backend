package com.dooh.signage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "empresas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    private String responsavel;

    private String telefone;

    private String whatsapp;

    @Column(nullable = false, unique = true)
    private String email;

    private String endereco;

    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(nullable = false)
    private String status = "ATIVO"; // ATIVO, INATIVO

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}

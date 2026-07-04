package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Midia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeOriginal;

    private String nomeArquivo;

    private String nomeExibicao;

    private String tipo;

    private String categoria;

    private Long tamanhoBytes;

    private String url;

    private String thumbnailUrl;

    private Integer largura;

    private Integer altura;

    private Long duracaoSegundos;

    private String status;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}
package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "telas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "codigo_unico", nullable = false, unique = true, length = 36)
    private String codigoUnico;

    @Column(name = "codigo_curto", unique = true, length = 30)
    private String codigoCurto;

    @Column(name = "versao_playlist", nullable = false)
    private Long versaoPlaylist = 1L;

    private String local;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private GrupoTela grupo;

    private String cidade;
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Orientacao orientacao = Orientacao.HORIZONTAL;

    private String polegadas = "LIVRE";
    private String resolucao = "1920x1080";

    @Column(nullable = false)
    private String status = "OFFLINE";

    @Column(name = "ultima_conexao")
    private ZonedDateTime ultimaConexao;

    private String ip;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Embedded
    private Telemetria telemetria;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Builder.Default
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Orientacao orientacao = Orientacao.HORIZONTAL;

    @Builder.Default
    private String polegadas = "LIVRE";

    @Builder.Default
    private String resolucao = "1920x1080";

    @Builder.Default
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

    @PrePersist
    @PreUpdate
    private void aplicarDefaults() {
        if (versaoPlaylist == null) {
            versaoPlaylist = 1L;
        }

        if (orientacao == null) {
            orientacao = Orientacao.HORIZONTAL;
        }

        if (polegadas == null || polegadas.isBlank()) {
            polegadas = "LIVRE";
        }

        if (resolucao == null || resolucao.isBlank()) {
            resolucao = "1920x1080";
        }

        if (status == null || status.isBlank()) {
            status = "OFFLINE";
        }
    }
}
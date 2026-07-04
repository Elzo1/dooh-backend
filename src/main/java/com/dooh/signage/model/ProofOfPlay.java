package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proof_of_play")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfPlay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telaId;
    private String telaCodigo;
    private String telaNome;

    private Long campanhaId;
    private String campanhaNome;

    private Long playlistId;

    private ZonedDateTime inicioExibicao;
    private ZonedDateTime fimExibicao;

    private Integer duracaoProgramada;
    private Integer duracaoReal;

    private String status;
    private String tipoMidia;
    private String urlMidia;

    private String cidade;
    private String temperatura;

    private String versaoPlayer;
    private String ip;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;
}
package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dispositivos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_uuid", nullable = false, unique = true, length = 80)
    private String deviceUuid;

    @Column(length = 6, unique = true)
    private String pin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tela_id")
    private Tela tela;

    @Column(nullable = false)
    private String status = "AGUARDANDO";

    @Column(nullable = false)
    private Boolean ativado = false;

    @Column(name = "expira_em")
    private ZonedDateTime expiraEm;

    @Column(name = "ultima_conexao")
    private ZonedDateTime ultimaConexao;

    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "versao_player")
    private String versaoPlayer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
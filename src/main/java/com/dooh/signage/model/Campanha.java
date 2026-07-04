package com.dooh.signage.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campanhas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Column(name = "url_midia", nullable = false, length = 500)
    private String urlMidia;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "dias_semana", nullable = false)
    private String diasSemana = "1,2,3,4,5,6,7";

    @Column(name = "hora_inicio")
    private LocalTime horaInicio = LocalTime.MIN;

    @Column(name = "hora_fim")
    private LocalTime horaFim = LocalTime.of(23, 59, 59);

    @Column(name = "tempo_exibicao", nullable = false)
    private Integer tempoExibicao = 15;

    @Column(nullable = false)
    private Integer prioridade = 1;

    @Column(nullable = false)
    private String status = "ATIVA";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
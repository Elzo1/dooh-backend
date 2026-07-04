package com.dooh.signage.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
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
@Table(name = "alertas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telaId;

    private String telaNome;

    private String tipo;

    private String nivel;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private Boolean lido;

    private Boolean resolvido;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;
}
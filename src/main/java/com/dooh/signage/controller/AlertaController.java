package com.dooh.signage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.AlertaResponse;
import com.dooh.signage.model.Alerta;
import com.dooh.signage.repository.AlertaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaRepository alertaRepository;

    @GetMapping
    public List<AlertaResponse> listar() {
        return alertaRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/{id}/resolver")
    public AlertaResponse resolver(@PathVariable Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado."));

        alerta.setResolvido(true);
        alerta.setLido(true);

        return toResponse(alertaRepository.save(alerta));
    }

    @PutMapping("/{id}/ler")
    public AlertaResponse marcarComoLido(@PathVariable Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado."));

        alerta.setLido(true);

        return toResponse(alertaRepository.save(alerta));
    }

    private AlertaResponse toResponse(Alerta alerta) {
        return AlertaResponse.builder()
                .id(alerta.getId())
                .telaId(alerta.getTelaId())
                .telaNome(alerta.getTelaNome())
                .tipo(alerta.getTipo())
                .nivel(alerta.getNivel())
                .mensagem(alerta.getMensagem())
                .lido(alerta.getLido())
                .resolvido(alerta.getResolvido())
                .createdAt(alerta.getCreatedAt())
                .build();
    }
}
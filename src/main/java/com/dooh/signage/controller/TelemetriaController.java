package com.dooh.signage.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.TelemetriaRequest;
import com.dooh.signage.model.Telemetria;
import com.dooh.signage.service.TelemetriaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/telemetria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    @PostMapping("/ping/{codigoUuid}")
    public ResponseEntity<Void> receberPing(
            @PathVariable String codigoUuid,
            @RequestBody TelemetriaRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String ip = extrairIpCliente(httpServletRequest);

        Telemetria telemetria = Telemetria.builder()
                .espacoLivreMb(valorLongOuZero(request.getEspacoLivreMb()))
                .memoriaUsoMb(valorLongOuZero(request.getMemoriaUsoMb()))
                .cpuUsoPorcento(valorBigDecimalOuZero(request.getCpuUsoPorcento()))
                .resolucao(textoOuNulo(request.getResolucao()))
                .orientacao(textoOuNulo(request.getOrientacao()))
                .versaoPlayer(textoOuNulo(request.getVersaoPlayer()))
                .campanhaAtual(textoOuNulo(request.getCampanhaAtual()))
                .uptimeSegundos(valorLongOuZero(request.getUptimeSegundos()))
                .userAgent(textoOuNulo(request.getUserAgent()))
                .build();

        telemetriaService.registrarPing(codigoUuid, ip, telemetria);

        return ResponseEntity.ok().build();
    }

    private String extrairIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");

        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }

    private Long valorLongOuZero(Long valor) {
        return valor != null ? valor : 0L;
    }

    private BigDecimal valorBigDecimalOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String textoOuNulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}
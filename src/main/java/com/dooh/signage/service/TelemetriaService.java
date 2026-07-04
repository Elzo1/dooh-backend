package com.dooh.signage.service;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.model.Tela;
import com.dooh.signage.model.Telemetria;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemetriaService {

    private final TelaRepository telaRepository;

    @Transactional
    public void registrarPing(String codigoUnico, String ip, Telemetria telemetria) {
        Tela tela = telaRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> new IllegalArgumentException("Tela não cadastrada: " + codigoUnico));

        tela.setIp(ip);
        tela.setStatus("ONLINE");
        tela.setUltimaConexao(ZonedDateTime.now());

        if (telemetria != null) {
            tela.setTelemetria(telemetria);
        }

        telaRepository.save(tela);
    }
}
package com.dooh.signage.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.model.Alerta;
import com.dooh.signage.model.Tela;
import com.dooh.signage.model.Telemetria;
import com.dooh.signage.repository.AlertaRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoramentoTelasService {

    private final TelaRepository telaRepository;
    private final AlertaRepository alertaRepository;

    private static final long LIMITE_OFFLINE_MINUTOS = 5;
    private static final double CPU_ALERTA = 85.0;
    private static final long MEMORIA_ALERTA_MB = 3500;
    private static final long ESPACO_BAIXO_MB = 1000;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void verificarTelas() {
        List<Tela> telas = telaRepository.findAll();
        ZonedDateTime agora = ZonedDateTime.now();

        for (Tela tela : telas) {
            verificarOffline(tela, agora);
            verificarTelemetria(tela);
            telaRepository.save(tela);
        }
    }

    private void verificarOffline(Tela tela, ZonedDateTime agora) {
        if (tela.getUltimaConexao() == null) {
            tela.setStatus("OFFLINE");
            criarAlertaSeNaoExiste(
                    tela,
                    "OFFLINE",
                    "CRITICO",
                    "Tela sem nenhuma conexão registrada."
            );
            return;
        }

        long minutosSemPing = Duration.between(tela.getUltimaConexao(), agora).toMinutes();

        if (minutosSemPing >= LIMITE_OFFLINE_MINUTOS) {
            tela.setStatus("OFFLINE");
            criarAlertaSeNaoExiste(
                    tela,
                    "OFFLINE",
                    "CRITICO",
                    "Tela sem ping há " + minutosSemPing + " minutos."
            );
        }
    }

    private void verificarTelemetria(Tela tela) {
        if (!"ONLINE".equalsIgnoreCase(tela.getStatus())) {
            return;
        }

        Telemetria telemetria = tela.getTelemetria();

        if (telemetria == null) {
            criarAlertaSeNaoExiste(
                    tela,
                    "SEM_TELEMETRIA",
                    "ALTO",
                    "Tela online, mas sem dados de telemetria."
            );
            return;
        }

        if (telemetria.getCpuUsoPorcento() != null
                && telemetria.getCpuUsoPorcento().doubleValue() >= CPU_ALERTA) {
            criarAlertaSeNaoExiste(
                    tela,
                    "CPU_ALTA",
                    "ALTO",
                    "CPU acima de " + CPU_ALERTA + "%."
            );
        }

        if (telemetria.getMemoriaUsoMb() != null
                && telemetria.getMemoriaUsoMb() >= MEMORIA_ALERTA_MB) {
            criarAlertaSeNaoExiste(
                    tela,
                    "MEMORIA_ALTA",
                    "MEDIO",
                    "Uso de memória acima de " + MEMORIA_ALERTA_MB + " MB."
            );
        }

        if (telemetria.getEspacoLivreMb() != null
                && telemetria.getEspacoLivreMb() <= ESPACO_BAIXO_MB) {
            criarAlertaSeNaoExiste(
                    tela,
                    "ESPACO_BAIXO",
                    "ALTO",
                    "Espaço livre abaixo de " + ESPACO_BAIXO_MB + " MB."
            );
        }

        if (telemetria.getCampanhaAtual() == null || telemetria.getCampanhaAtual().isBlank()) {
            criarAlertaSeNaoExiste(
                    tela,
                    "SEM_CAMPANHA",
                    "MEDIO",
                    "Player online, mas sem campanha atual."
            );
        }
    }

    private void criarAlertaSeNaoExiste(Tela tela, String tipo, String nivel, String mensagem) {
        if (tela == null || tela.getId() == null) {
            return;
        }

        boolean alertaAberto = alertaRepository
                .findFirstByTelaIdAndTipoAndResolvidoFalse(tela.getId(), tipo)
                .isPresent();

        if (alertaAberto) {
            return;
        }

        Alerta alerta = Alerta.builder()
                .telaId(tela.getId())
                .telaNome(tela.getNome())
                .tipo(tipo)
                .nivel(nivel)
                .mensagem(mensagem)
                .lido(false)
                .resolvido(false)
                .build();

        alertaRepository.save(alerta);
    }
}
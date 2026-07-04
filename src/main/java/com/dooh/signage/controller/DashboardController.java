package com.dooh.signage.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.DashboardResumoResponse;
import com.dooh.signage.model.Tela;
import com.dooh.signage.model.Telemetria;
import com.dooh.signage.repository.CampanhaRepository;
import com.dooh.signage.repository.EmpresaRepository;
import com.dooh.signage.repository.MidiaRepository;
import com.dooh.signage.repository.PlaylistRepository;
import com.dooh.signage.repository.ProofOfPlayRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TelaRepository telaRepository;
    private final CampanhaRepository campanhaRepository;
    private final EmpresaRepository empresaRepository;
    private final PlaylistRepository playlistRepository;
    private final MidiaRepository midiaRepository;
    private final ProofOfPlayRepository proofOfPlayRepository;

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Bahia");

    @GetMapping("/resumo")
    public DashboardResumoResponse resumo() {
        List<Tela> telas = telaRepository.findAll();

        Long totalTelas = (long) telas.size();
        Long telasOnline = telaRepository.countByStatus("ONLINE");
        Long telasOffline = Math.max(0, totalTelas - telasOnline);

        ZonedDateTime inicioHoje = ZonedDateTime.now(ZONA_PADRAO).toLocalDate().atStartOfDay(ZONA_PADRAO);
        ZonedDateTime fimHoje = inicioHoje.plusDays(1).minusNanos(1);

        return DashboardResumoResponse.builder()
                .totalTelas(totalTelas)
                .telasOnline(telasOnline)
                .telasOffline(telasOffline)

                .totalCampanhas(campanhaRepository.count())
                .campanhasAtivas(campanhaRepository.countByStatus("ATIVA"))

                .empresas(empresaRepository.count())
                .playlists(playlistRepository.count())
                .uploads(midiaRepository.count())

                .proofOfPlayHoje(proofOfPlayRepository.countByCreatedAtBetween(inicioHoje, fimHoje))
                .proofOfPlaySucesso(proofOfPlayRepository.countByStatus("SUCESSO"))

                .cpuMedia(calcularCpuMedia(telas))
                .memoriaMediaMb(calcularMemoriaMedia(telas))
                .espacoLivreMedioMb(calcularEspacoLivreMedio(telas))

                .faturamentoEstimado(BigDecimal.valueOf(48600))

                .ultimasTelas(montarUltimasTelas(telas))
                .build();
    }

    private BigDecimal calcularCpuMedia(List<Tela> telas) {
        List<BigDecimal> valores = telas.stream()
                .map(Tela::getTelemetria)
                .filter(Objects::nonNull)
                .map(Telemetria::getCpuUsoPorcento)
                .filter(Objects::nonNull)
                .toList();

        if (valores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal soma = valores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return soma.divide(BigDecimal.valueOf(valores.size()), 2, RoundingMode.HALF_UP);
    }

    private Long calcularMemoriaMedia(List<Tela> telas) {
        return Math.round(
                telas.stream()
                        .map(Tela::getTelemetria)
                        .filter(Objects::nonNull)
                        .map(Telemetria::getMemoriaUsoMb)
                        .filter(Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0)
        );
    }

    private Long calcularEspacoLivreMedio(List<Tela> telas) {
        return Math.round(
                telas.stream()
                        .map(Tela::getTelemetria)
                        .filter(Objects::nonNull)
                        .map(Telemetria::getEspacoLivreMb)
                        .filter(Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0)
        );
    }

    private List<DashboardResumoResponse.TelaStatusDto> montarUltimasTelas(List<Tela> telas) {
        return telas.stream()
                .sorted(Comparator.comparing(
                        Tela::getUltimaConexao,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(5)
                .map(this::toTelaStatusDto)
                .toList();
    }

    private DashboardResumoResponse.TelaStatusDto toTelaStatusDto(Tela tela) {
        Telemetria telemetria = tela.getTelemetria();

        return DashboardResumoResponse.TelaStatusDto.builder()
                .id(tela.getId())
                .nome(tela.getNome())
                .status(tela.getStatus())
                .ip(tela.getIp())
                .ultimaConexao(tela.getUltimaConexao() != null ? tela.getUltimaConexao().toString() : null)
                .campanhaAtual(telemetria != null ? telemetria.getCampanhaAtual() : null)
                .versaoPlayer(telemetria != null ? telemetria.getVersaoPlayer() : null)
                .build();
    }
}
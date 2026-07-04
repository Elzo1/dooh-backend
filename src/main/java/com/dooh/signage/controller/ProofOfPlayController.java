package com.dooh.signage.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.ProofOfPlayRequest;
import com.dooh.signage.dto.ProofOfPlayResponse;
import com.dooh.signage.model.ProofOfPlay;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.ProofOfPlayRepository;
import com.dooh.signage.repository.TelaRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proof-of-play")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProofOfPlayController {

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Bahia");

    private final ProofOfPlayRepository proofOfPlayRepository;
    private final TelaRepository telaRepository;

    @PostMapping
    public ResponseEntity<ProofOfPlayResponse> registrar(
            @RequestBody ProofOfPlayRequest request,
            HttpServletRequest servletRequest
    ) {
        Tela tela = buscarTela(request.getTelaCodigo());

        ProofOfPlay registro = ProofOfPlay.builder()
                .telaId(tela != null ? tela.getId() : null)
                .telaCodigo(request.getTelaCodigo())
                .telaNome(tela != null ? tela.getNome() : null)
                .campanhaId(request.getCampanhaId())
                .campanhaNome(request.getCampanhaNome())
                .playlistId(request.getPlaylistId())
                .inicioExibicao(parseZonedDateTime(request.getInicioExibicao()))
                .fimExibicao(parseZonedDateTime(request.getFimExibicao()))
                .duracaoProgramada(valorOuPadrao(request.getDuracaoProgramada(), 0))
                .duracaoReal(valorOuPadrao(request.getDuracaoReal(), 0))
                .status(textoOuPadrao(request.getStatus(), "SUCESSO").toUpperCase())
                .tipoMidia(textoOuPadrao(request.getTipoMidia(), "DESCONHECIDO").toUpperCase())
                .urlMidia(request.getUrlMidia())
                .cidade(request.getCidade())
                .temperatura(request.getTemperatura())
                .versaoPlayer(request.getVersaoPlayer())
                .ip(resolverIp(servletRequest))
                .build();

        return ResponseEntity.ok(toResponse(proofOfPlayRepository.save(registro)));
    }

    @GetMapping
    public ResponseEntity<List<ProofOfPlayResponse>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tipoMidia,
            @RequestParam(required = false) String telaCodigo,
            @RequestParam(required = false) Long campanhaId,
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(defaultValue = "300") Integer limite
    ) {
        ZonedDateTime inicioPeriodo = parseDataInicio(inicio);
        ZonedDateTime fimPeriodo = parseDataFim(fim);
        int limiteSeguro = Math.min(Math.max(limite != null ? limite : 300, 1), 1000);

        List<ProofOfPlayResponse> response = proofOfPlayRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(item -> filtrarBusca(item, busca))
                .filter(item -> status == null || status.isBlank() || status.equalsIgnoreCase(item.getStatus()))
                .filter(item -> tipoMidia == null || tipoMidia.isBlank() || tipoMidia.equalsIgnoreCase(item.getTipoMidia()))
                .filter(item -> telaCodigo == null || telaCodigo.isBlank() || telaCodigo.equalsIgnoreCase(item.getTelaCodigo()))
                .filter(item -> campanhaId == null || campanhaId.equals(item.getCampanhaId()))
                .filter(item -> inicioPeriodo == null || item.getCreatedAt() == null || !item.getCreatedAt().isBefore(inicioPeriodo))
                .filter(item -> fimPeriodo == null || item.getCreatedAt() == null || !item.getCreatedAt().isAfter(fimPeriodo))
                .limit(limiteSeguro)
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private Tela buscarTela(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        return telaRepository.findByCodigoUnico(codigo)
                .or(() -> telaRepository.findByCodigoCurtoIgnoreCase(codigo))
                .orElse(null);
    }

    private ProofOfPlayResponse toResponse(ProofOfPlay item) {
        return ProofOfPlayResponse.builder()
                .id(item.getId())
                .telaId(item.getTelaId())
                .telaNome(item.getTelaNome())
                .telaCodigo(item.getTelaCodigo())
                .campanhaId(item.getCampanhaId())
                .campanhaNome(item.getCampanhaNome())
                .playlistId(item.getPlaylistId())
                .duracaoProgramada(item.getDuracaoProgramada())
                .duracaoReal(item.getDuracaoReal())
                .status(item.getStatus())
                .tipoMidia(item.getTipoMidia())
                .urlMidia(item.getUrlMidia())
                .cidade(item.getCidade())
                .temperatura(item.getTemperatura())
                .versaoPlayer(item.getVersaoPlayer())
                .ip(item.getIp())
                .inicioExibicao(item.getInicioExibicao())
                .fimExibicao(item.getFimExibicao())
                .createdAt(item.getCreatedAt())
                .build();
    }

    private ZonedDateTime parseZonedDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return ZonedDateTime.parse(value);
    }

    private ZonedDateTime parseDataInicio(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value).atStartOfDay(ZONA_PADRAO);
    }

    private ZonedDateTime parseDataFim(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value).atTime(LocalTime.MAX).atZone(ZONA_PADRAO);
    }

    private String textoFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private String textoOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }

        return valor.trim();
    }

    private Integer valorOuPadrao(Integer valor, Integer padrao) {
        return valor != null ? valor : padrao;
    }

    private String resolverIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
    
    
    private boolean filtrarBusca(ProofOfPlay item, String busca) {
        if (busca == null || busca.isBlank()) {
            return true;
        }

        String termo = busca.toLowerCase();

        String texto = String.join(" ",
                item.getTelaNome() != null ? item.getTelaNome() : "",
                item.getTelaCodigo() != null ? item.getTelaCodigo() : "",
                item.getCampanhaNome() != null ? item.getCampanhaNome() : "",
                item.getCidade() != null ? item.getCidade() : "",
                item.getIp() != null ? item.getIp() : ""
        ).toLowerCase();

        return texto.contains(termo);
    }
}

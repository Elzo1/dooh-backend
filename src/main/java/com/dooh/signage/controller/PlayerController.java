package com.dooh.signage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.dooh.signage.dto.PlayerCheckResponse;
import com.dooh.signage.dto.PlaylistResponse;
import com.dooh.signage.model.Campanha;
import com.dooh.signage.model.PlaylistItem;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.TelaRepository;
import com.dooh.signage.service.PlayerPlaylistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final TelaRepository telaRepository;
    private final PlayerPlaylistService playerPlaylistService;

    @GetMapping("/{codigo}")
    @Transactional(readOnly = true)
    public ResponseEntity<PlaylistResponse> obterPlaylist(@PathVariable String codigo) {
        Tela tela = buscarTelaPorCodigo(codigo);

        List<PlaylistResponse.PlaylistItemDto> itensDto = playerPlaylistService
                .buscarItensValidos(tela)
                .stream()
                .map(this::toDto)
                .toList();

        PlaylistResponse response = PlaylistResponse.builder()
                .telaCodigo(codigoPrincipal(tela))
                .nomeTela(tela.getNome())
                .orientacao(tela.getOrientacao() != null ? tela.getOrientacao().name() : null)
                .versaoPlaylist(versaoPlaylist(tela))
                .itens(itensDto)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{codigo}/check")
    @Transactional(readOnly = true)
    public ResponseEntity<PlayerCheckResponse> verificarAtualizacao(
            @PathVariable String codigo,
            @RequestParam(required = false) Long versaoAtual
    ) {
        Tela tela = buscarTelaPorCodigo(codigo);

        Long versaoBanco = versaoPlaylist(tela);
        Long versaoPlayer = versaoAtual != null ? versaoAtual : 0L;

        PlayerCheckResponse response = PlayerCheckResponse.builder()
                .telaCodigo(codigoPrincipal(tela))
                .versaoAtual(versaoBanco)
                .update(!versaoBanco.equals(versaoPlayer))
                .build();

        return ResponseEntity.ok(response);
    }

    private Tela buscarTelaPorCodigo(String codigo) {
        return telaRepository.findByCodigoUnico(codigo)
                .or(() -> telaRepository.findByCodigoCurtoIgnoreCase(codigo))
                .orElseThrow(() -> new IllegalArgumentException("Código de tela inválido: " + codigo));
    }

    private String codigoPrincipal(Tela tela) {
        if (tela.getCodigoCurto() != null && !tela.getCodigoCurto().isBlank()) {
            return tela.getCodigoCurto();
        }

        return tela.getCodigoUnico();
    }

    private Long versaoPlaylist(Tela tela) {
        return tela.getVersaoPlaylist() != null ? tela.getVersaoPlaylist() : 1L;
    }

    private PlaylistResponse.PlaylistItemDto toDto(PlaylistItem item) {
        Campanha campanha = item.getCampanha();

        return PlaylistResponse.PlaylistItemDto.builder()
                .campanhaId(campanha.getId())
                .nomeCampanha(campanha.getNome())
                .urlMidia(campanha.getUrlMidia())
                .tempoExibicao(campanha.getTempoExibicao())
                .ordem(item.getOrdem())
                .prioridade(campanha.getPrioridade())
                .diasSemana(campanha.getDiasSemana())
                .horaInicio(campanha.getHoraInicio() != null ? campanha.getHoraInicio().toString() : null)
                .horaFim(campanha.getHoraFim() != null ? campanha.getHoraFim().toString() : null)
                .dataInicio(campanha.getDataInicio() != null ? campanha.getDataInicio().toString() : null)
                .dataFim(campanha.getDataFim() != null ? campanha.getDataFim().toString() : null)
                .build();
    }
}
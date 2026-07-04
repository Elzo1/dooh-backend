package com.dooh.signage.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.model.Playlist;
import com.dooh.signage.model.PlaylistItem;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerPlaylistService {

    private final PlaylistRepository playlistRepository;
    private final CampanhaValidador campanhaValidador;

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Bahia");

    @Transactional(readOnly = true)
    public List<PlaylistItem> buscarItensValidos(Tela tela) {
        Playlist playlist = playlistRepository.findFirstByTelaAndAtivaTrue(tela)
                .orElse(null);

        if (playlist == null || playlist.getItens() == null) {
            return List.of();
        }

        LocalDate hoje = LocalDate.now(ZONA_PADRAO);
        LocalTime agora = LocalTime.now(ZONA_PADRAO);
        int diaSemanaAtual = converterDiaSemanaParaNumero(hoje.getDayOfWeek());

        return playlist.getItens()
                .stream()
                .filter(item -> item != null && item.getCampanha() != null)
                .filter(item -> campanhaValidador.podeExibir(
                        item.getCampanha(),
                        hoje,
                        agora,
                        diaSemanaAtual
                ))
                .sorted(
                        Comparator
                                .comparing(
                                        (PlaylistItem item) -> item.getCampanha().getPrioridade(),
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                                .thenComparing(
                                        PlaylistItem::getOrdem,
                                        Comparator.nullsLast(Integer::compareTo)
                                )
                )
                .toList();
    }

    private int converterDiaSemanaParaNumero(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
}
package com.dooh.signage.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.model.Campanha;
import com.dooh.signage.model.GrupoTela;
import com.dooh.signage.model.Playlist;
import com.dooh.signage.model.PlaylistItem;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.CampanhaRepository;
import com.dooh.signage.repository.GrupoTelaRepository;
import com.dooh.signage.repository.PlaylistItemRepository;
import com.dooh.signage.repository.PlaylistRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoPublicacaoService {

    private final GrupoTelaRepository grupoTelaRepository;
    private final TelaRepository telaRepository;
    private final CampanhaRepository campanhaRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final PlayerUpdateService playerUpdateService;

    @Transactional
    public List<Playlist> publicarGrupo(Long grupoId, List<Long> campanhaIdsOrdenados) {
        GrupoTela grupo = grupoTelaRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        List<Tela> telas = telaRepository.findByGrupo(grupo);

        if (telas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma tela vinculada ao grupo.");
        }

        List<Campanha> campanhas = resolverCampanhas(grupo, campanhaIdsOrdenados);

        if (campanhas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma campanha ativa para publicar.");
        }

        return telas.stream()
                .map(tela -> publicarNaTela(tela, campanhas))
                .toList();
    }

    private List<Campanha> resolverCampanhas(GrupoTela grupo, List<Long> campanhaIdsOrdenados) {
        if (campanhaIdsOrdenados != null && !campanhaIdsOrdenados.isEmpty()) {
            return campanhaIdsOrdenados.stream()
                    .map(id -> campanhaRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada: " + id)))
                    .filter(campanha -> "ATIVA".equalsIgnoreCase(campanha.getStatus()))
                    .toList();
        }

        return grupo.getCampanhas()
                .stream()
                .filter(campanha -> "ATIVA".equalsIgnoreCase(campanha.getStatus()))
                .sorted(
                        Comparator
                                .comparing(Campanha::getPrioridade, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Campanha::getId)
                )
                .toList();
    }

    private Playlist publicarNaTela(Tela tela, List<Campanha> campanhas) {
        Playlist playlist = playlistRepository.findFirstByTelaAndAtivaTrue(tela)
                .orElseGet(() -> playlistRepository.save(
                        Playlist.builder()
                                .tela(tela)
                                .nome("Playlist Principal")
                                .ativa(true)
                                .build()
                ));

        playlistItemRepository.deleteByPlaylist(playlist);
        playlistItemRepository.flush();

        int ordem = 1;

        for (Campanha campanha : campanhas) {
            PlaylistItem item = PlaylistItem.builder()
                    .playlist(playlist)
                    .campanha(campanha)
                    .ordem(ordem++)
                    .build();

            playlistItemRepository.save(item);
        }

        playlistItemRepository.flush();

        playerUpdateService.notificarPlaylistAtualizada(tela.getId());

        return playlist;
    }
}
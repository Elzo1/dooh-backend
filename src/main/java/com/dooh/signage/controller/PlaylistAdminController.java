package com.dooh.signage.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.GrupoPublicacaoRequest;
import com.dooh.signage.dto.PlaylistItemRequest;
import com.dooh.signage.dto.PlaylistItemResponse;
import com.dooh.signage.dto.PlaylistResponseAdmin;
import com.dooh.signage.dto.ReordenarPlaylistRequest;
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
import com.dooh.signage.service.GrupoPublicacaoService;
import com.dooh.signage.service.PlayerUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/playlists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaylistAdminController {

    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final TelaRepository telaRepository;
    private final CampanhaRepository campanhaRepository;
    private final GrupoTelaRepository grupoTelaRepository;
    private final PlayerUpdateService playerUpdateService;
    private final GrupoPublicacaoService grupoPublicacaoService;

    @GetMapping("/tela/{telaId}")
    public PlaylistResponseAdmin buscarPorTela(@PathVariable Long telaId) {
        Tela tela = telaRepository.findById(telaId)
                .orElseThrow(() -> new IllegalArgumentException("Tela não encontrada."));

        Playlist playlist = buscarOuCriarPlaylist(tela);
        return toResponse(playlist);
    }

    @PostMapping("/{playlistId}/itens")
    @Transactional
    public PlaylistResponseAdmin adicionarItem(
            @PathVariable Long playlistId,
            @RequestBody PlaylistItemRequest request
    ) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist não encontrada."));

        Campanha campanha = campanhaRepository.findById(request.getCampanhaId())
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));

        adicionarCampanhaNaPlaylist(playlist, campanha, request.getOrdem());
        playlistItemRepository.flush();

        playerUpdateService.notificarPlaylistAtualizada(playlist.getTela().getId());

        return toResponse(playlist);
    }

    @PostMapping("/grupos/{grupoId}/campanhas/{campanhaId}")
    @Transactional
    public List<PlaylistResponseAdmin> adicionarCampanhaAoGrupo(
            @PathVariable Long grupoId,
            @PathVariable Long campanhaId
    ) {
        GrupoTela grupo = grupoTelaRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        Campanha campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));

        List<Tela> telasDoGrupo = telaRepository.findByGrupo(grupo);

        if (telasDoGrupo.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma tela encontrada neste grupo.");
        }

        List<PlaylistResponseAdmin> respostas = telasDoGrupo.stream()
                .map(tela -> {
                    Playlist playlist = buscarOuCriarPlaylist(tela);
                    adicionarCampanhaNaPlaylistSeNaoExiste(playlist, campanha);
                    playerUpdateService.notificarPlaylistAtualizada(tela.getId());
                    return toResponse(playlist);
                })
                .toList();

        playlistItemRepository.flush();

        return respostas;
    }

    @PostMapping("/grupos/{grupoId}/publicar")
    @Transactional
    public List<PlaylistResponseAdmin> publicarGrupo(
            @PathVariable Long grupoId,
            @RequestBody(required = false) GrupoPublicacaoRequest request
    ) {
        List<Long> campanhaIds = request != null ? request.getCampanhaIds() : null;

        List<Playlist> playlists = grupoPublicacaoService.publicarGrupo(grupoId, campanhaIds);

        return playlists.stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/{playlistId}/reordenar")
    @Transactional
    public PlaylistResponseAdmin reordenar(
            @PathVariable Long playlistId,
            @RequestBody ReordenarPlaylistRequest request
    ) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist não encontrada."));

        if (request.getItens() == null || request.getItens().isEmpty()) {
            return toResponse(playlist);
        }

        List<PlaylistItem> itensDaPlaylist =
                playlistItemRepository.findByPlaylistOrderByOrdemAsc(playlist);

        for (PlaylistItem item : itensDaPlaylist) {
            item.setOrdem(item.getOrdem() + 1000);
        }

        playlistItemRepository.saveAll(itensDaPlaylist);
        playlistItemRepository.flush();

        for (ReordenarPlaylistRequest.ItemOrdem itemOrdem : request.getItens()) {
            PlaylistItem item = playlistItemRepository.findById(itemOrdem.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item da playlist não encontrado."));

            if (!item.getPlaylist().getId().equals(playlistId)) {
                throw new IllegalArgumentException("Item não pertence a esta playlist.");
            }

            item.setOrdem(itemOrdem.getOrdem());
            playlistItemRepository.save(item);
        }

        playlistItemRepository.flush();

        playerUpdateService.notificarPlaylistAtualizada(playlist.getTela().getId());

        return toResponse(playlist);
    }

    @DeleteMapping("/itens/{itemId}")
    @Transactional
    public void removerItem(@PathVariable Long itemId) {
        PlaylistItem item = playlistItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item da playlist não encontrado."));

        Long telaId = item.getPlaylist().getTela().getId();

        playlistItemRepository.delete(item);
        playlistItemRepository.flush();

        playerUpdateService.notificarPlaylistAtualizada(telaId);
    }

    private Playlist buscarOuCriarPlaylist(Tela tela) {
        return playlistRepository.findFirstByTelaAndAtivaTrue(tela)
                .orElseGet(() -> playlistRepository.save(
                        Playlist.builder()
                                .tela(tela)
                                .nome("Playlist Principal")
                                .ativa(true)
                                .build()
                ));
    }

    private void adicionarCampanhaNaPlaylist(Playlist playlist, Campanha campanha, Integer ordemSolicitada) {
        Integer ordem = ordemSolicitada != null ? ordemSolicitada : proximaOrdem(playlist);

        PlaylistItem item = PlaylistItem.builder()
                .playlist(playlist)
                .campanha(campanha)
                .ordem(ordem)
                .build();

        playlistItemRepository.save(item);
    }

    private void adicionarCampanhaNaPlaylistSeNaoExiste(Playlist playlist, Campanha campanha) {
        boolean jaExiste = playlistItemRepository.findByPlaylistOrderByOrdemAsc(playlist)
                .stream()
                .anyMatch(item -> item.getCampanha() != null
                        && item.getCampanha().getId().equals(campanha.getId()));

        if (!jaExiste) {
            adicionarCampanhaNaPlaylist(playlist, campanha, null);
        }
    }

    private Integer proximaOrdem(Playlist playlist) {
        return playlistItemRepository.findByPlaylistOrderByOrdemAsc(playlist)
                .stream()
                .map(PlaylistItem::getOrdem)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private PlaylistResponseAdmin toResponse(Playlist playlist) {
        List<PlaylistItemResponse> itens = playlistItemRepository
                .findByPlaylistOrderByOrdemAsc(playlist)
                .stream()
                .sorted(Comparator.comparing(PlaylistItem::getOrdem))
                .map(this::toItemResponse)
                .toList();

        return PlaylistResponseAdmin.builder()
                .id(playlist.getId())
                .nome(playlist.getNome())
                .ativa(playlist.getAtiva())
                .telaId(playlist.getTela() != null ? playlist.getTela().getId() : null)
                .telaNome(playlist.getTela() != null ? playlist.getTela().getNome() : null)
                .itens(itens)
                .build();
    }

    private PlaylistItemResponse toItemResponse(PlaylistItem item) {
        Campanha campanha = item.getCampanha();

        return PlaylistItemResponse.builder()
                .id(item.getId())
                .campanhaId(campanha != null ? campanha.getId() : null)
                .campanhaNome(campanha != null ? campanha.getNome() : null)
                .urlMidia(campanha != null ? campanha.getUrlMidia() : null)
                .tempoExibicao(campanha != null ? campanha.getTempoExibicao() : null)
                .ordem(item.getOrdem())
                .status(campanha != null ? campanha.getStatus() : null)
                .build();
    }
}
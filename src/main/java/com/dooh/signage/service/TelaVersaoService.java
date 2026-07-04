package com.dooh.signage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelaVersaoService {

    private final TelaRepository telaRepository;

    @Transactional
    public void marcarPlaylistAtualizada(Tela tela) {
        if (tela == null || tela.getId() == null) return;

        Tela telaBanco = telaRepository.findById(tela.getId()).orElse(null);
        if (telaBanco == null) return;

        Long versaoAtual = telaBanco.getVersaoPlaylist() != null
                ? telaBanco.getVersaoPlaylist()
                : 1L;

        telaBanco.setVersaoPlaylist(versaoAtual + 1);
        telaRepository.save(telaBanco);
    }
}
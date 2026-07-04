package com.dooh.signage.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.dooh.signage.dto.PlayerUpdateMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarPlaylistAtualizada(Long telaId) {
        PlayerUpdateMessage message = PlayerUpdateMessage.builder()
                .tipo("PLAYLIST_ATUALIZADA")
                .telaId(telaId)
                .mensagem("A playlist foi atualizada.")
                .build();

        messagingTemplate.convertAndSend("/topic/player/tela/" + telaId, message);

        messagingTemplate.convertAndSend("/topic/player/atualizacoes", message);
    }
}
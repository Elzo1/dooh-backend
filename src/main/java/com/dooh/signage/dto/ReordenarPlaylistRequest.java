package com.dooh.signage.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReordenarPlaylistRequest {
    private List<ItemOrdem> itens;

    @Data
    public static class ItemOrdem {
        private Long itemId;
        private Integer ordem;
    }
}
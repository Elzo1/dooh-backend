package com.dooh.signage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlist_itens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"playlist_id", "ordem"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @Column(nullable = false)
    private Integer ordem; // Posicao de reproducao estrita
}

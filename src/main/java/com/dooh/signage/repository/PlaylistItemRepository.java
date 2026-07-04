package com.dooh.signage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Playlist;
import com.dooh.signage.model.PlaylistItem;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {

    List<PlaylistItem> findByPlaylistOrderByOrdemAsc(Playlist playlist);

    void deleteByPlaylist(Playlist playlist);
}
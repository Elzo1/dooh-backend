package com.dooh.signage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Playlist;
import com.dooh.signage.model.Tela;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    Optional<Playlist> findFirstByTelaAndAtivaTrue(Tela tela);

}
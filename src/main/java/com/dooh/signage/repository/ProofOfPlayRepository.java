package com.dooh.signage.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.ProofOfPlay;

@Repository
public interface ProofOfPlayRepository extends JpaRepository<ProofOfPlay, Long> {

    List<ProofOfPlay> findTop100ByOrderByCreatedAtDesc();

    List<ProofOfPlay> findAllByOrderByCreatedAtDesc();

    Long countByStatus(String status);

    Long countByCreatedAtBetween(ZonedDateTime inicio, ZonedDateTime fim);
}
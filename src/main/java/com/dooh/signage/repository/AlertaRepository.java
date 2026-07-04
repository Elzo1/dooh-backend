package com.dooh.signage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Alerta;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findTop50ByOrderByCreatedAtDesc();

    Long countByResolvidoFalse();

    Optional<Alerta> findFirstByTelaIdAndTipoAndResolvidoFalse(Long telaId, String tipo);
}
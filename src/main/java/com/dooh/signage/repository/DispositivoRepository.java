package com.dooh.signage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Dispositivo;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    Optional<Dispositivo> findByDeviceUuid(String deviceUuid);

    Optional<Dispositivo> findByPin(String pin);

    boolean existsByPin(String pin);
}
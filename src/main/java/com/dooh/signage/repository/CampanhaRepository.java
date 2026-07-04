package com.dooh.signage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Campanha;
import com.dooh.signage.model.Empresa;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    Long countByStatus(String status);

    Long countByEmpresa(Empresa empresa);

    List<Campanha> findByStatusOrderByNomeAsc(String status);

    List<Campanha> findByEmpresa(Empresa empresa);

    List<Campanha> findByEmpresaOrderByNomeAsc(Empresa empresa);
}
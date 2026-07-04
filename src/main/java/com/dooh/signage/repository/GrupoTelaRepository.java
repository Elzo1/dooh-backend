package com.dooh.signage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.GrupoTela;

@Repository
public interface GrupoTelaRepository extends JpaRepository<GrupoTela, Long> {

    List<GrupoTela> findByAtivoTrueOrderByNomeAsc();

    List<GrupoTela> findByEmpresa(Empresa empresa);

    Long countByEmpresa(Empresa empresa);
}
package com.dooh.signage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Empresa;
import com.dooh.signage.model.GrupoTela;
import com.dooh.signage.model.Tela;

@Repository
public interface TelaRepository extends JpaRepository<Tela, Long> {

    Optional<Tela> findByCodigoUnico(String codigoUnico);

    Optional<Tela> findByCodigoCurtoIgnoreCase(String codigoCurto);

    boolean existsByCodigoCurtoIgnoreCase(String codigoCurto);

    Long countByStatus(String status);

    List<Tela> findByGrupo(GrupoTela grupo);

    Long countByGrupo(GrupoTela grupo);

    List<Tela> findByEmpresa(Empresa empresa);

    Long countByEmpresa(Empresa empresa);

    Long countByEmpresaAndStatus(Empresa empresa, String status);
}
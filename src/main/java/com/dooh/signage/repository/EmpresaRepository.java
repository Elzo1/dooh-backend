package com.dooh.signage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dooh.signage.model.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByEmail(String email);

    Optional<Empresa> findByCnpj(String cnpj);

    Boolean existsByEmail(String email);

    Boolean existsByCnpj(String cnpj);

    Long countByStatus(String status);

    List<Empresa> findByStatusOrderByNomeFantasiaAsc(String status);

    List<Empresa> findByNomeFantasiaContainingIgnoreCaseOrRazaoSocialContainingIgnoreCaseOrCnpjContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByNomeFantasiaAsc(
            String nomeFantasia,
            String razaoSocial,
            String cnpj,
            String email
    );
}
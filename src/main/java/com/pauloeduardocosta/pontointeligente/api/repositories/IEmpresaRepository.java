package com.pauloeduardocosta.pontointeligente.api.repositories;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IEmpresaRepository extends JpaRepository<Empresa, Long> {

    @Transactional(readOnly = true)
    Optional<Empresa> findByCnpj(String cnpj);
}

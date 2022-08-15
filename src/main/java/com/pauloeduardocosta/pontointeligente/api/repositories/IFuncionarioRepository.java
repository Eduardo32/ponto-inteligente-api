package com.pauloeduardocosta.pontointeligente.api.repositories;

import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface IFuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    Optional<Funcionario> findByCpfOrEmail(String cpf, String email);
}

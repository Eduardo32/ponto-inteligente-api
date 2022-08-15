package com.pauloeduardocosta.pontointeligente.api.services.impl;

import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.repositories.IFuncionarioRepository;
import com.pauloeduardocosta.pontointeligente.api.services.IFuncionarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FuncionarioService implements IFuncionarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuncionarioService.class);

    @Autowired
    private IFuncionarioRepository funcionarioRepository;

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        LOGGER.info("Persistindo funcionário: {}", funcionario);
        return funcionarioRepository.save(funcionario);
    }

    @Override
    public Optional<Funcionario> buscarPorCpf(String cpf) {
        LOGGER.info("Buscando funcionário pelo CPF {}", cpf);
        return funcionarioRepository.findByCpf(cpf);
    }

    @Override
    public Optional<Funcionario> buscarPorEmail(String email) {
        LOGGER.info("Buscando funcionário pelo email {}", email);
        return funcionarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Funcionario> buscarPorId(Long id) {
        LOGGER.info("Buscando funcionário pelo IDl {}", id);
        return funcionarioRepository.findById(id);
    }
}

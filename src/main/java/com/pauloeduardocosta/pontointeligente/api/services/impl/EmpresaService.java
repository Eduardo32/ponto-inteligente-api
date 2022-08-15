package com.pauloeduardocosta.pontointeligente.api.services.impl;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.repositories.IEmpresaRepository;
import com.pauloeduardocosta.pontointeligente.api.services.IEmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpresaService implements IEmpresaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpresaService.class);

    @Autowired
    private IEmpresaRepository empresaRepository;

    @Override
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        LOGGER.info("Buscando uma empresa para o CNPJ {}", cnpj);
        return empresaRepository.findByCnpj(cnpj);
    }

    @Override
    public Empresa salvar(Empresa empresa) {
        LOGGER.info("Salvando empresa {}", empresa);
        return empresaRepository.save(empresa);
    }
}

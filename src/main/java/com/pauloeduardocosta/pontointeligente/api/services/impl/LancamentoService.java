package com.pauloeduardocosta.pontointeligente.api.services.impl;

import com.pauloeduardocosta.pontointeligente.api.entities.Lancamento;
import com.pauloeduardocosta.pontointeligente.api.repositories.ILancamentoRepository;
import com.pauloeduardocosta.pontointeligente.api.services.ILancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LancamentoService implements ILancamentoService {

    private static final Logger log = LoggerFactory.getLogger(LancamentoService.class);

    @Autowired
    private ILancamentoRepository lancamentoRepository;

    @Override
    public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
        log.info("Buscando lançamentos para o funcionário ID {}", funcionarioId);
        return lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
    }

    @Override
    public Optional<Lancamento> buscarPorId(Long id) {
        log.info("Buscando um lançamento pelo ID {}", id);
        return lancamentoRepository.findById(id);
    }

    @Override
    public Lancamento salvar(Lancamento lancamento) {
        log.info("Persistindo o lançamento: {}", lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    public void remover(Long id) {
        log.info("Removendo o lançamento ID {}", id);
        lancamentoRepository.deleteById(id);
    }
}

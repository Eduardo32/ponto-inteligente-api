package com.pauloeduardocosta.pontointeligente.api.services;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;

import java.util.Optional;

public interface IEmpresaService {

    /**
     * Retorna uma empresa dado um CNPJ.
     *
     * @param cnpj CNPJ de empresa que deseja buscar
     * @return Optional<Empresa> Empresa encontrada com o CNPJ informado
     */
    Optional<Empresa> buscarPorCnpj(String cnpj);

    /**
     * Cadastra uma nova empresa na base de dados.
     *
     * @param empresa Empresa que deseja salvar na base de dados
     * @return Empresa Empresa salva
     */
    Empresa salvar(Empresa empresa);
}

package com.pauloeduardocosta.pontointeligente.api.services;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.repositories.IEmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class IEmpresaServiceTest {

    @MockBean
    private IEmpresaRepository empresaRepository;

    @Autowired
    private IEmpresaService empresaService;

    private static final String CNPJ = "51463645000100";

    @BeforeEach
    public void setUp() throws Exception {
        BDDMockito.given(this.empresaRepository.findByCnpj(Mockito.anyString())).willReturn(Optional.of(new Empresa()));
        BDDMockito.given(this.empresaRepository.save(Mockito.any(Empresa.class))).willReturn(new Empresa());
    }

    @Test
    public void testBuscarEmpresaPorCnpj() {
        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(CNPJ);

        assertTrue(empresa.isPresent());
    }

    @Test
    public void testPersistirEmpresa() {
        Empresa empresa = this.empresaService.salvar(new Empresa());

        assertNotNull(empresa);
    }

}
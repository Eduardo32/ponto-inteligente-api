package com.pauloeduardocosta.pontointeligente.api.repositories;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@ActiveProfiles("test")
class IEmpresaRepositoryTest {

    @Autowired
    private IEmpresaRepository empresaRepository;

    private static final String CNPJ = "51463645000100";

    @BeforeEach
    void setUp() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa de Exemplo");
        empresa.setCnpj(CNPJ);
        this.empresaRepository.save(empresa);
    }

    @AfterEach
    void tearDown() {
        this.empresaRepository.deleteAll();
    }

    @Test
    void findByCnpj() {
        Optional<Empresa> empresa = this.empresaRepository.findByCnpj(CNPJ);
        if(empresa.isEmpty()) {
            fail("Não encontrou a empresa");
        }

        assertNotNull(empresa.get());
        assertEquals(CNPJ, empresa.get().getCnpj());
    }
}
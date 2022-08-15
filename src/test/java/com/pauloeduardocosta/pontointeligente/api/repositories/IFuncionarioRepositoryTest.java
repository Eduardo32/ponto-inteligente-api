package com.pauloeduardocosta.pontointeligente.api.repositories;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.enums.EPerfil;
import com.pauloeduardocosta.pontointeligente.api.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class IFuncionarioRepositoryTest {

    @Autowired
    private IFuncionarioRepository funcionarioRepository;

    @Autowired
    private IEmpresaRepository empresaRepository;

    private static final String EMAIL = "email@email.com";
    private static final String CPF = "24291173474";

    @BeforeEach
    void setUp() {
        Empresa empresa = this.empresaRepository.save(mockEmpresa());
        this.funcionarioRepository.save(mockFuncionario(empresa));
    }

    @AfterEach
    void tearDown() {
        this.empresaRepository.deleteAll();
    }

    @Test
    void findByCpf() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpf(CPF);
        if(funcionario.isEmpty()) {
            fail("Não encotrou o funcionario com cpf " + CPF);
        }

        assertNotNull(funcionario.get());
    }

    @Test
    void findByEmail() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByEmail(EMAIL);
        if(funcionario.isEmpty()) {
            fail("Não encotrou o funcionario com email " + EMAIL);
        }

        assertNotNull(funcionario.get());
    }

    @Test
    void findByCpfOrEmail() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);
        if(funcionario.isEmpty()) {
            fail("Não encotrou o funcionario com CPF " + CPF + " ou email " + EMAIL);
        }

        assertNotNull(funcionario.get());
    }

    @Test
    void findByCpfOrEmailComCpfInvalido() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail("12345678910", EMAIL);
        if(funcionario.isEmpty()) {
            fail("Não encotrou o funcionario com CPF " + CPF + " ou email " + EMAIL);
        }

        assertNotNull(funcionario.get());
    }

    @Test
    void findByCpfOrEmailComEmailInvalido() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, "invalido@email.com");
        if(funcionario.isEmpty()) {
            fail("Não encotrou o funcionario com CPF " + CPF + " ou email " + EMAIL);
        }

        assertNotNull(funcionario.get());
    }

    @Test
    void findByCpfOrEmailComAmbosInvalidos() {
        Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail("12345678910", "invalido@email.com");
        if(funcionario.isPresent()) {
            fail("Encotrou o funcionario com CPF e email invalido");
        }
    }

    private Funcionario mockFuncionario(Empresa empresa) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Fulano");
        funcionario.setPerfil(EPerfil.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
        funcionario.setCpf(CPF);
        funcionario.setEmail(EMAIL);
        funcionario.setEmpresa(empresa);
        return funcionario;
    }

    private Empresa mockEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa de Exemplo");
        empresa.setCnpj("51463645000100");
        return empresa;
    }
}
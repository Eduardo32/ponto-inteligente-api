package com.pauloeduardocosta.pontointeligente.api.repositories;

import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.entities.Lancamento;
import com.pauloeduardocosta.pontointeligente.api.enums.EPerfil;
import com.pauloeduardocosta.pontointeligente.api.enums.ETipo;
import com.pauloeduardocosta.pontointeligente.api.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ILancamentoRepositoryTest {

    @Autowired
    private ILancamentoRepository lancamentoRepository;

    @Autowired
    private IFuncionarioRepository funcionarioRepository;

    @Autowired
    private IEmpresaRepository empresaRepository;

    private Long fincionarioId;

    @BeforeEach
    void setUp() {
        Empresa empresa = this.empresaRepository.save(mockEmpresa());
        Funcionario funcionario = this.funcionarioRepository.save(mockFuncionario(empresa));
        this.fincionarioId = funcionario.getId();

        this.lancamentoRepository.save(mockLancamento(funcionario));
        this.lancamentoRepository.save(mockLancamento(funcionario));
    }

    @AfterEach
    void tearDown() {
        this.empresaRepository.deleteAll();
    }

    @Test
    void findByFuncionarioId() {
        List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(fincionarioId);

        assertNotNull(lancamentos);
        assertEquals(2, lancamentos.size());
    }

    @Test
    void testFindByFuncionarioId() {
        Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(fincionarioId,
                PageRequest.of(0, 1));

        assertNotNull(lancamentos);
        assertEquals(2, lancamentos.getTotalElements());
    }

    private Funcionario mockFuncionario(Empresa empresa) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Fulano");
        funcionario.setPerfil(EPerfil.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
        funcionario.setCpf("24291173474");
        funcionario.setEmail("email@email.com");
        funcionario.setEmpresa(empresa);
        return funcionario;
    }

    private Empresa mockEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa de Exemplo");
        empresa.setCnpj("51463645000100");
        return empresa;
    }

    private Lancamento mockLancamento(Funcionario funcionario) {
        Lancamento lancamento = new Lancamento();
        lancamento.setData(new Date());
        lancamento.setTipo(ETipo.INICIO_ALMOCO);
        lancamento.setFuncionario(funcionario);
        return lancamento;
    }
}
package com.pauloeduardocosta.pontointeligente.api.services;

import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.repositories.IFuncionarioRepository;
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
class IFuncionarioServiceTest {

    @MockBean
    private IFuncionarioRepository funcionarioRepository;

    @Autowired
    private IFuncionarioService funcionarioService;

    @BeforeEach
    public void setUp() throws Exception {
        BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
        BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong())).willReturn(Optional.of(new Funcionario()));
        BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString())).willReturn(Optional.of(new Funcionario()));
        BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString())).willReturn(Optional.of(new Funcionario()));
    }

    @Test
    public void testPersistirFuncionario() {
        Funcionario funcionario = this.funcionarioService.salvar(new Funcionario());
        assertNotNull(funcionario);
    }

    @Test
    public void testBuscarFuncionarioPorId() {
        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(1L);
        assertTrue(funcionario.isPresent());
    }

    @Test
    public void testBuscarFuncionarioPorEmail() {
        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorEmail("email@email.com");
        assertTrue(funcionario.isPresent());
    }

    @Test
    public void testBuscarFuncionarioPorCpf() {
        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorCpf("24291173474");
        assertTrue(funcionario.isPresent());
    }
}
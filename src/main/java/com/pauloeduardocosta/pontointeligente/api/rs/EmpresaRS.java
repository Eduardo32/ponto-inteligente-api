package com.pauloeduardocosta.pontointeligente.api.rs;

import com.pauloeduardocosta.pontointeligente.api.dtos.CadastroPJDTO;
import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.enums.EPerfil;
import com.pauloeduardocosta.pontointeligente.api.response.Response;
import com.pauloeduardocosta.pontointeligente.api.services.IEmpresaService;
import com.pauloeduardocosta.pontointeligente.api.services.IFuncionarioService;
import com.pauloeduardocosta.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/empresa")
@CrossOrigin(origins = "*")
public class EmpresaRS {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpresaRS.class);

    @Autowired
    private IFuncionarioService funcionarioService;

    @Autowired
    private IEmpresaService empresaService;

    @PostMapping
    public ResponseEntity<Response<CadastroPJDTO>> cadastrar(@Validated @RequestBody CadastroPJDTO cadastroPJDTO,
                                                             BindingResult result) throws NoSuchAlgorithmException {
        LOGGER.info("Cadastrando PJ {}", cadastroPJDTO.toString());
        Response<CadastroPJDTO> response = new Response<>();
        validarDadosExistentes(cadastroPJDTO, result);
        Empresa empresa = Empresa.builder()
                .cnpj(cadastroPJDTO.getCnpj())
                .razaoSocial(cadastroPJDTO.getRazaoSocial())
                .build();
        Funcionario funcionario = Funcionario.builder()
                .nome(cadastroPJDTO.getNome())
                .email(cadastroPJDTO.getEmail())
                .cpf(cadastroPJDTO.getCpf())
                .perfil(EPerfil.ROLE_ADMIN)
                .senha(PasswordUtils.gerarBCrypt(cadastroPJDTO.getSenha()))
                .build();

        if(result.hasErrors()) {
            LOGGER.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(objectError -> response.getErrors().add(objectError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        empresaService.salvar(empresa);
        funcionario.setEmpresa(empresa);
        funcionarioService.salvar(funcionario);

        response.setData(
                CadastroPJDTO.builder()
                        .id(funcionario.getId())
                        .nome(funcionario.getNome())
                        .email(funcionario.getEmail())
                        .cpf(funcionario.getCpf())
                        .razaoSocial(funcionario.getEmpresa().getRazaoSocial())
                        .cnpj(funcionario.getEmpresa().getCnpj())
                        .build());

        return ResponseEntity.ok(response);
    }

    private void validarDadosExistentes(CadastroPJDTO cadastroPJDTO, BindingResult result) {
        empresaService.buscarPorCnpj(cadastroPJDTO.getCnpj())
                .ifPresent(empresa -> result.addError(new ObjectError("empresa", "Empresa já existente")));
        funcionarioService.buscarPorCpf(cadastroPJDTO.getCpf())
                .ifPresent(empresa -> result.addError(new ObjectError("funcionario", "CPF já existente")));
        funcionarioService.buscarPorEmail(cadastroPJDTO.getEmail())
                .ifPresent(empresa -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }
}

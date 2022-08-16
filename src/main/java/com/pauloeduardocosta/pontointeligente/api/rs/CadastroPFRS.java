package com.pauloeduardocosta.pontointeligente.api.rs;

import com.pauloeduardocosta.pontointeligente.api.dtos.CadastroPFDTO;
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

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/pessoa/fisica")
@CrossOrigin(origins = "*")
public class CadastroPFRS {

    private static final Logger LOGGER = LoggerFactory.getLogger(CadastroPFRS.class);

    @Autowired
    private IFuncionarioService funcionarioService;

    @Autowired
    private IEmpresaService empresaService;

    @PostMapping
    public ResponseEntity<Response<CadastroPFDTO>> cadastrar(@Validated @RequestBody CadastroPFDTO cadastroPFDTO,
                                                             BindingResult result) throws NoSuchAlgorithmException {
        LOGGER.info("Cadastrando PF: {}", cadastroPFDTO.toString());
        Response<CadastroPFDTO> response = new Response<>();
        validarDadosExistentes(cadastroPFDTO, result);
        Funcionario funcionario = converterDTOParaFuncionario(cadastroPFDTO);

        if(result.hasErrors()) {
            LOGGER.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
            result.getAllErrors().forEach(objectError -> response.getErrors().add(objectError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cadastroPFDTO.getCnpj());
        empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
        funcionarioService.salvar(funcionario);

        response.setData(converterFuncionarioParaDTO(funcionario));
        return ResponseEntity.ok(response);
    }

    private CadastroPFDTO converterFuncionarioParaDTO(Funcionario funcionario) {
        CadastroPFDTO cadastroPFDTO = CadastroPFDTO.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .cpf(funcionario.getCpf())
                .cnpj(funcionario.getEmpresa().getCnpj())
                .build();
        funcionario.getQtdHorasAlmocoOpt()
                .ifPresent(qtdHorasAlmoco -> cadastroPFDTO.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt()
                .ifPresent(qtsHorasTrabalhoDia -> cadastroPFDTO.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtsHorasTrabalhoDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> cadastroPFDTO.setValorHora(Optional.of(valorHora.toString())));

        return cadastroPFDTO;
    }

    private Funcionario converterDTOParaFuncionario(CadastroPFDTO cadastroPFDTO) {
        Funcionario funcionario = Funcionario.builder()
                .nome(cadastroPFDTO.getNome())
                .email(cadastroPFDTO.getEmail())
                .cpf(cadastroPFDTO.getCpf())
                .perfil(EPerfil.ROLE_USUARIO)
                .senha(PasswordUtils.gerarBCrypt(cadastroPFDTO.getSenha()))
                .build();
        cadastroPFDTO.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        cadastroPFDTO.getQtdHorasTrabalhoDia()
                .ifPresent(qtsHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtsHorasTrabalhoDia)));
        cadastroPFDTO.getValorHora()
                .ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        return funcionario;
    }

    private void validarDadosExistentes(CadastroPFDTO cadastroPFDTO, BindingResult result) {
        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cadastroPFDTO.getCnpj());
        if(empresa.isEmpty()) {
            result.addError(new ObjectError("empresa", "Empresa não cadastrada"));
        }
        funcionarioService.buscarPorCpf(cadastroPFDTO.getCpf())
                .ifPresent(funcionario -> result.addError(new ObjectError("funcionario", "CPF já existente")));
        funcionarioService.buscarPorEmail(cadastroPFDTO.getEmail())
                .ifPresent(funcionario -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }
}


























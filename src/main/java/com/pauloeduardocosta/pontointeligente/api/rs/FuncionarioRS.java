package com.pauloeduardocosta.pontointeligente.api.rs;

import com.pauloeduardocosta.pontointeligente.api.dtos.EmpresaDTO;
import com.pauloeduardocosta.pontointeligente.api.dtos.FuncionarioDTO;
import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.response.Response;
import com.pauloeduardocosta.pontointeligente.api.services.IEmpresaService;
import com.pauloeduardocosta.pontointeligente.api.services.impl.FuncionarioService;
import com.pauloeduardocosta.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionario")
@CrossOrigin(origins = "*")
public class FuncionarioRS {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuncionarioRS.class);

    @Autowired
    private FuncionarioService funcionarioService;

    /**
     * Atualiza os dados de um funcionário.
     *
     * @param id
     * @param funcionarioDto
     * @param result
     * @return ResponseEntity<Response<FuncionarioDto>>
     * @throws NoSuchAlgorithmException
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<FuncionarioDTO>> atualizar(@PathVariable("id") Long id,
                                                              @Validated @RequestBody FuncionarioDTO funcionarioDto, BindingResult result
    ) throws NoSuchAlgorithmException {
        LOGGER.info("Atualizando funcionário: {}", funcionarioDto.toString());
        Response<FuncionarioDTO> response = new Response<>();

        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
        if (!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionário não encontrado."));
        }

        this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

        if (result.hasErrors()) {
            LOGGER.error("Erro validando funcionário: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.funcionarioService.salvar(funcionario.get());
        response.setData(montarDTO(funcionario.get()));

        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os dados do funcionário com base nos dados encontrados no DTO.
     *
     * @param funcionario
     * @param funcionarioDto
     * @param result
     * @throws NoSuchAlgorithmException
     */
    private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDTO funcionarioDto, BindingResult result)
            throws NoSuchAlgorithmException {
        funcionario.setNome(funcionarioDto.getNome());

        if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
            this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
                    .ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
            funcionario.setEmail(funcionarioDto.getEmail());
        }

        funcionario.setQtdHorasAlmoco(null);
        funcionarioDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

        funcionario.setQtdHorasTrabalhoDia(null);
        funcionarioDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

        funcionario.setValorHora(null);
        funcionarioDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        if (funcionarioDto.getSenha().isPresent()) {
            funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
        }
    }

    /**
     * Retorna um DTO com os dados de um funcionário.
     *
     * @param funcionario
     * @return FuncionarioDto
     */
    private FuncionarioDTO montarDTO(Funcionario funcionario) {
        FuncionarioDTO funcionarioDto = FuncionarioDTO.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .build();
        funcionario.getQtdHorasAlmocoOpt().ifPresent(
                qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
                qtdHorasTrabDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));

        return funcionarioDto;
    }

}

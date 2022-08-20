package com.pauloeduardocosta.pontointeligente.api.rs;

import com.pauloeduardocosta.pontointeligente.api.dtos.LancamentoDTO;
import com.pauloeduardocosta.pontointeligente.api.entities.Funcionario;
import com.pauloeduardocosta.pontointeligente.api.entities.Lancamento;
import com.pauloeduardocosta.pontointeligente.api.enums.ETipo;
import com.pauloeduardocosta.pontointeligente.api.response.Response;
import com.pauloeduardocosta.pontointeligente.api.services.IFuncionarioService;
import com.pauloeduardocosta.pontointeligente.api.services.ILancamentoService;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamento")
@CrossOrigin(origins = "*")
public class LancamentoRS {

    private static final Logger LOGGER = LoggerFactory.getLogger(LancamentoRS.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ILancamentoService lancamentoService;

    @Autowired
    private IFuncionarioService funcionarioService;

    @Value("${paginacao.qtd_por_pagina}")
    private int qtdPorPagina;

    /**
     * Retorna a listagem de lançamentos de um funcionário.
     *
     * @param funcionarioId
     * @return ResponseEntity<Response<LancamentoDto>>
     */
    @GetMapping(value = "/funcionario/{funcionarioId}")
    public ResponseEntity<Response<Page<LancamentoDTO>>> listarPorFuncionarioId(
            @PathVariable("funcionarioId") Long funcionarioId,
            @RequestParam(value = "pag", defaultValue = "0") int pag,
            @RequestParam(value = "ord", defaultValue = "id") String ord,
            @RequestParam(value = "dir", defaultValue = "DESC") String dir) {
        LOGGER.info("Buscando lançamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
        Response<Page<LancamentoDTO>> response = new Response<>();

        PageRequest pageRequest = PageRequest.of(pag, qtdPorPagina, Sort.Direction.valueOf(dir), ord);
        Page<Lancamento> lancamentos = lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
        Page<LancamentoDTO> lancamentosDto = lancamentos.map(lancamento -> montarDTO(lancamento));

        response.setData(lancamentosDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna um lançamento por ID.
     *
     * @param id
     * @return ResponseEntity<Response<LancamentoDto>>
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDTO>> listarPorId(@PathVariable("id") Long id) {
        LOGGER.info("Buscando lançamento por ID: {}", id);
        Response<LancamentoDTO> response = new Response<>();
        Optional<Lancamento> lancamento = lancamentoService.buscarPorId(id);

        if (!lancamento.isPresent()) {
            LOGGER.info("Lançamento não encontrado para o ID: {}", id);
            response.getErrors().add("Lançamento não encontrado para o id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(montarDTO(lancamento.get()));
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona um novo lançamento.
     *
     * @param lancamentoDto
     * @param result
     * @return ResponseEntity<Response<LancamentoDTO>>
     * @throws ParseException
     */
    @PostMapping
    public ResponseEntity<Response<LancamentoDTO>> adicionar(@Validated @RequestBody LancamentoDTO lancamentoDto,
                                                             BindingResult result) throws ParseException {
        LOGGER.info("Adicionando lançamento: {}", lancamentoDto.toString());
        Response<LancamentoDTO> response = new Response<>();
        validarFuncionario(lancamentoDto, result);
        Lancamento lancamento = converterDtoParaLancamento(lancamentoDto, result);

        if (result.hasErrors()) {
            LOGGER.error("Erro validando lançamento: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        lancamento = lancamentoService.salvar(lancamento);
        response.setData(montarDTO(lancamento));
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os dados de um lançamento.
     *
     * @param id
     * @param lancamentoDto
     * @return ResponseEntity<Response<Lancamento>>
     * @throws ParseException
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDTO>> atualizar(@PathVariable("id") Long id,
                                                             @Validated @RequestBody LancamentoDTO lancamentoDto,
                                                             BindingResult result) throws ParseException {
        LOGGER.info("Atualizando lançamento: {}", lancamentoDto.toString());
        Response<LancamentoDTO> response = new Response<>();
        validarFuncionario(lancamentoDto, result);
        lancamentoDto.setId(Optional.of(id));
        Lancamento lancamento = converterDtoParaLancamento(lancamentoDto, result);

        if (result.hasErrors()) {
            LOGGER.error("Erro validando lançamento: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        lancamento = lancamentoService.salvar(lancamento);
        response.setData(montarDTO(lancamento));
        return ResponseEntity.ok(response);
    }

    /**
     * Remove um lançamento por ID.
     *
     * @param id
     * @return ResponseEntity<Response<Lancamento>>
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
        LOGGER.info("Removendo lançamento: {}", id);
        Response<String> response = new Response<String>();
        Optional<Lancamento> lancamento = lancamentoService.buscarPorId(id);

        if (!lancamento.isPresent()) {
            LOGGER.info("Erro ao remover devido ao lançamento ID: {} ser inválido.", id);
            response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        lancamentoService.remover(id);
        return ResponseEntity.ok(new Response<>());
    }

    /**
     * Valida um funcionário, verificando se ele é existente e válido no
     * sistema.
     *
     * @param lancamentoDto
     * @param result
     */
    private void validarFuncionario(LancamentoDTO lancamentoDto, BindingResult result) {
        if (lancamentoDto.getFuncionarioId() == null) {
            result.addError(new ObjectError("funcionario", "Funcionário não informado."));
            return;
        }

        LOGGER.info("Validando funcionário id {}: ", lancamentoDto.getFuncionarioId());
        Optional<Funcionario> funcionario = funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
        if (!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."));
        }
    }

    /**
     * Converte uma entidade lançamento para seu respectivo DTO.
     *
     * @param lancamento
     * @return LancamentoDto
     */
    private LancamentoDTO montarDTO(Lancamento lancamento) {
        return LancamentoDTO.builder()
                .id(Optional.of(lancamento.getId()))
                .data(dateFormat.format(lancamento.getData()))
                .tipo(lancamento.getTipo().toString())
                .descricao(lancamento.getDescricao())
                .localizacao(lancamento.getLocalizacao())
                .funcionarioId(lancamento.getFuncionario().getId())
                .build();
    }

    /**
     * Converte um LancamentoDto para uma entidade Lancamento.
     *
     * @param lancamentoDto
     * @param result
     * @return Lancamento
     * @throws ParseException
     */
    private Lancamento converterDtoParaLancamento(LancamentoDTO lancamentoDto, BindingResult result) throws ParseException {
        Lancamento lancamento = new Lancamento();

        if (lancamentoDto.getId().isPresent()) {
            Optional<Lancamento> lanc = lancamentoService.buscarPorId(lancamentoDto.getId().get());
            if (lanc.isPresent()) {
                lancamento = lanc.get();
            } else {
                result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
            }
        } else {
            lancamento.setFuncionario(new Funcionario());
            lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
        }

        lancamento.setDescricao(lancamentoDto.getDescricao());
        lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
        lancamento.setData(dateFormat.parse(lancamentoDto.getData()));

        if (EnumUtils.isValidEnum(ETipo.class, lancamentoDto.getTipo())) {
            lancamento.setTipo(ETipo.valueOf(lancamentoDto.getTipo()));
        } else {
            result.addError(new ObjectError("tipo", "Tipo inválido."));
        }

        return lancamento;
    }
}

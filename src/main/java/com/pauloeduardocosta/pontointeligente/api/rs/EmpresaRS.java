package com.pauloeduardocosta.pontointeligente.api.rs;

import com.pauloeduardocosta.pontointeligente.api.dtos.EmpresaDTO;
import com.pauloeduardocosta.pontointeligente.api.entities.Empresa;
import com.pauloeduardocosta.pontointeligente.api.response.Response;
import com.pauloeduardocosta.pontointeligente.api.services.IEmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresa")
@CrossOrigin(origins = "*")
public class EmpresaRS {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpresaRS.class);

    @Autowired
    private IEmpresaService empresaService;

    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDTO>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
        LOGGER.info("Buscando empresa por CNPJ: {}", cnpj);
        Response<EmpresaDTO> response = new Response<>();
        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

        if (empresa.isEmpty()) {
            LOGGER.info("Empresa não encontrada para o CNPJ: {}", cnpj);
            response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(montarDTO(empresa.get()));
        return ResponseEntity.ok(response);
    }

    private EmpresaDTO montarDTO(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .cnpj(empresa.getCnpj())
                .razaoSocial(empresa.getRazaoSocial())
                .build();
    }
}

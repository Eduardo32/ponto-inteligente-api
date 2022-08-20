package com.pauloeduardocosta.pontointeligente.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmpresaDTO {

    private Long id;
    private String razaoSocial;
    private String cnpj;
}

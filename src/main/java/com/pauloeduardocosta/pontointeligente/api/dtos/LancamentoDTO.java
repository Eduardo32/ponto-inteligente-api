package com.pauloeduardocosta.pontointeligente.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LancamentoDTO {

    @Setter
    private Optional<Long> id = Optional.empty();

    @NotEmpty(message = "Data não pode ser vazia.")
    private String data;
    private String tipo;
    private String descricao;
    private String localizacao;
    private Long funcionarioId;
}

package com.pauloeduardocosta.pontointeligente.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FuncionarioDTO {

    private Long id;

    @NotEmpty(message = "Nome não pode ser vazio.")
    @Length(min = 3, max = 200, message = "Nome deve conter entre 3 e 200 caracteres.")
    private String nome;

    @NotEmpty(message = "Email não pode ser vazio.")
    @Length(min = 5, max = 200, message = "Email deve conter entre 5 e 200 caracteres.")
    @Email(message="Email inválido.")
    private String email;
    private Optional<String> senha = Optional.empty();

    @Setter
    private Optional<String> valorHora = Optional.empty();

    @Setter
    private Optional<String> qtdHorasTrabalhoDia = Optional.empty();

    @Setter
    private Optional<String> qtdHorasAlmoco = Optional.empty();
}

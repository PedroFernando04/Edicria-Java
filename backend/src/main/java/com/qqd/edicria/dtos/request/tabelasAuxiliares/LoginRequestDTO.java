package com.qqd.edicria.dtos.request.tabelasAuxiliares;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    String email,

    @NotBlank(message = "Senha é obrigatório")
    String senha
) {}

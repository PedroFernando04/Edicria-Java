package com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UsuarioRequestDTO (

    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "O email informado deve ser válido")
    String email,

    @NotBlank(message = "Senha é obrigatório")
    String senha,

    @NotNull(message = "Gênero é obrigatório")
    EnumGeneroPessoa genero,

    @NotNull(message = "País é obrigatório")
    EnumPaises pais,

    @NotNull(message = "Data do nascimento é obrigatório")
    @Past(message = "A data de nascimento deve estar no passado")
    LocalDate dataNascimento
) {}

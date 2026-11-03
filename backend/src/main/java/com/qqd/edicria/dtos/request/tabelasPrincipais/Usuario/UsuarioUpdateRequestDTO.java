package com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UsuarioUpdateRequestDTO(

        String nome,

        @Email(message = "O email informado deve ser válido")
        String email,

        String senha,

        EnumGeneroPessoa genero,

        EnumPaises pais,

        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {}

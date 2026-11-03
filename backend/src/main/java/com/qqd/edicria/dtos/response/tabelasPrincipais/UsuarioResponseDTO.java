package com.qqd.edicria.dtos.response.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;

import java.time.LocalDate;

public record UsuarioResponseDTO(

        Long id,

        String nome,

        String email,

        EnumGeneroPessoa genero,

        EnumPaises pais,

        LocalDate dataNascimento,

        Boolean adm

) {}
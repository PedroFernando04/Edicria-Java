package com.qqd.edicria.dtos.request.tabelasPrincipais.Autor;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutorRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "País é obrigatório")
        EnumPaises paisOrigem,

        @NotNull(message = "Gênero é obrigatório")
        EnumGeneroPessoa genero
) {}

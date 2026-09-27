package com.qqd.edicria.dtos.request.tabelasPrincipais.Editora;

import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditoraRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "País é obrigatório")
        EnumPaises paisOrigem
) {}

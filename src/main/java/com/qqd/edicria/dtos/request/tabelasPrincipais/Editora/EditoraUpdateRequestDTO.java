package com.qqd.edicria.dtos.request.tabelasPrincipais.Editora;

import com.qqd.edicria.entities.enums.EnumPaises;

public record EditoraUpdateRequestDTO(

        String nome,

        EnumPaises paisOrigem
) {
}

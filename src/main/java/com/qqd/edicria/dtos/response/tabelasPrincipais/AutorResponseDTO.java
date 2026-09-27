package com.qqd.edicria.dtos.response.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;

public record AutorResponseDTO(

        Long id,

        String nome,

        EnumPaises paisOrigem,

        EnumGeneroPessoa genero
) {
}

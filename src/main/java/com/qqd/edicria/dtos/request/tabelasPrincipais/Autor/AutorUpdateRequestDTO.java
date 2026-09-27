package com.qqd.edicria.dtos.request.tabelasPrincipais.Autor;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;

public record AutorUpdateRequestDTO(

        String nome,

        EnumPaises paisOrigem,

        EnumGeneroPessoa genero
) {}

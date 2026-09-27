package com.qqd.edicria.dtos.response.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumPaises;

public record EditoraResponseDTO(

        Long id,

        String nome,

        EnumPaises paisOrigem
) {}

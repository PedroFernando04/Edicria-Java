package com.qqd.edicria.dtos.response.tabelasPrincipais;

import com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro;
import com.qqd.edicria.entities.enums.livros.EnumFormatoLivro;

import java.time.LocalDate;

public record LivroResponseDTO(

        Long id,

        String titulo,

        AutorResponseDTO autor,

        EditoraResponseDTO editora,

        LocalDate dataLancamento,

        EnumCategoriasLivro categoria,

        EnumFormatoLivro formato
) {}

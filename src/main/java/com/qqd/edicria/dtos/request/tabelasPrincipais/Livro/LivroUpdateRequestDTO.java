package com.qqd.edicria.dtos.request.tabelasPrincipais.Livro;

import com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro;
import com.qqd.edicria.entities.enums.livros.EnumFormatoLivro;

import java.time.LocalDate;

public record LivroUpdateRequestDTO(

        String titulo,

        String autor,

        String editora,

        LocalDate dataLancamento,

        EnumCategoriasLivro categoria,

        EnumFormatoLivro formato
) {
}

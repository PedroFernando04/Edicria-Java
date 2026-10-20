package com.qqd.edicria.dtos.request.tabelasPrincipais.Livro;

import com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro;
import com.qqd.edicria.entities.enums.livros.EnumFormatoLivro;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LivroRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotNull(message = "Autor é obrigatório")
        String autor,

        @NotNull(message = "Editora é obrigatório")
        String editora,

        @NotNull(message = "Data de Lançamento é obrigatório")
        LocalDate dataLancamento,

        @NotNull(message = "Categoria é obrigatório")
        EnumCategoriasLivro categoria,

        @NotNull(message = "Formato é obrigatório")
        EnumFormatoLivro formato,

        @NotNull(message = "O número de páginas é obrigatório")
        @Min(value = 1, message = "O Livro deve conter ao menos uma página")
        Integer numeroPaginas

) {}

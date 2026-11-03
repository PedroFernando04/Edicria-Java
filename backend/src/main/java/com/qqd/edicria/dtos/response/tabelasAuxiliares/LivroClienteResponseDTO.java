package com.qqd.edicria.dtos.response.tabelasAuxiliares;

import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;
import com.qqd.edicria.entities.enums.EnumLingua;
import com.qqd.edicria.entities.enums.livros.EnumStatusLivro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroClienteResponseDTO(

        Long id,

        UsuarioResponseDTO usuario,

        LivroResponseDTO livro,

        EnumStatusLivro statusLivro,

        LocalDate dataInicioLeitura,

        LocalDate dataTerminoLeitura,

        BigDecimal nota,

        String resenha,

        EnumLingua linguaLida
) {}

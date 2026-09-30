package com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente;

import com.qqd.edicria.entities.enums.EnumLingua;
import com.qqd.edicria.entities.enums.livros.EnumStatusLivro;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroClienteUpdateRequestDTO(

        String usuario,

        String livro,

        EnumStatusLivro statusLivro,

        @PastOrPresent(message = "O início da leitura não pode ser previsto")
        LocalDate dataInicioLeitura,

        @PastOrPresent(message = "O término da leitura não pode ser previsto")
        LocalDate dataTerminoLeitura,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal nota,

        @Size(min = 1, max = 3000)
        String resenha,

        EnumLingua linguaLida
) {}

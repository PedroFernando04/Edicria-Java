package com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente;

import com.qqd.edicria.entities.enums.EnumLingua;
import com.qqd.edicria.entities.enums.livros.EnumStatusLivro;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroClienteRequestDTO(

        @NotBlank(message = "Usuário é obrigatório")
        String usuarioEmail,

        @NotBlank(message = "Livro é obrigatório")
        String livro,

        @NotNull(message = "Status é obrigatório")
        EnumStatusLivro statusLivro,

        @PastOrPresent(message = "A leitura não pode ser iniciada no futuro")
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

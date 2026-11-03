package com.qqd.edicria.entities.tabelasAuxiliares;

import com.qqd.edicria.entities.enums.EnumLingua;
import com.qqd.edicria.entities.enums.livros.EnumStatusLivro;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class LivroCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Usuario usuario;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Livro livro;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumStatusLivro status;

    @Column(nullable = true)
    private LocalDate dataInicioLeitura;

    @Column(nullable = true)
    private LocalDate dataTerminoLeitura;

    @Column(nullable = true, precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(nullable = true, length = 3000)
    private String resenha;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private EnumLingua linguaLida;
}

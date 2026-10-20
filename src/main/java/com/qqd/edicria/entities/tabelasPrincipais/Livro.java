package com.qqd.edicria.entities.tabelasPrincipais;

import com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro;
import com.qqd.edicria.entities.enums.livros.EnumFormatoLivro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Autor autor;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Editora editora;

    @Column(nullable = false)
    private LocalDate dataLancamento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumCategoriasLivro categoria;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumFormatoLivro formato;

    @Column(nullable = false)
    private Integer numeroPaginas;

}

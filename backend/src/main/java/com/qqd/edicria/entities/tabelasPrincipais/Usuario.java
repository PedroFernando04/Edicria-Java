package com.qqd.edicria.entities.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumGeneroPessoa genero;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumPaises paisOrigem;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column
    private Boolean adm = false;
}

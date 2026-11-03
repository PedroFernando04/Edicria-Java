package com.qqd.edicria.entities.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private EnumPaises paisOrigem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumGeneroPessoa genero;
}

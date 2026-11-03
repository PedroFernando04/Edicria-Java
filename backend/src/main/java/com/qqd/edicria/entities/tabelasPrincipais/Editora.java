package com.qqd.edicria.entities.tabelasPrincipais;

import com.qqd.edicria.entities.enums.EnumPaises;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Editora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column
    @Enumerated(EnumType.STRING)
    private EnumPaises paisOrigem;
}

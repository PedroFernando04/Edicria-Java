package com.qqd.edicria.entities.enums.livros;

import lombok.Getter;

@Getter
public enum EnumStatusLivro {

    LIDO ("Lido"),
    LENDO ("Lendo"),
    LISTA_DE_DESEJOS ("Lista de desesjos"),
    DESISTI ("Desisti");

    private String descricao;

    EnumStatusLivro(String descricao) {
        this.descricao = descricao;
    }
}

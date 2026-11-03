package com.qqd.edicria.entities.enums.livros;

import lombok.Getter;

@Getter
public enum EnumCategoriasLivro {

    HQ("História em Quadrinhos"),
    GRAPHIC_NOVEL("Graphic Novel"),
    MANGA("Mangá"),
    LIVRO("Livro");

    private String descricao;

    EnumCategoriasLivro(String descricao) {
        this.descricao = descricao;
    }
}
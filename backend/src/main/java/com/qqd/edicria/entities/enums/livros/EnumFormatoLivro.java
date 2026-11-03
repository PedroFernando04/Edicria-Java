package com.qqd.edicria.entities.enums.livros;

import lombok.Getter;

@Getter
public enum EnumFormatoLivro {

    DIGITAL("Digital"),
    FISICO("Físico"),
    EBOOK("E-book"),
    AUDIOBOOK("Audiobook");

    private String descricao;

    EnumFormatoLivro(String descricao) {
        this.descricao = descricao;
    }
}

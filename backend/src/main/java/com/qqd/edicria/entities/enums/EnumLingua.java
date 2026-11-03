package com.qqd.edicria.entities.enums;

import lombok.Getter;

@Getter
public enum EnumLingua {

    PORTUGUES ("Português"),
    INGLES ("Inglês"),
    ITALIANO ("Italiano"),
    ESPANHOL ("Espanhol"),
    FRANCES ("Francês");

    private String descricao;

    EnumLingua(String descricao){
        this.descricao = descricao;
    }
}

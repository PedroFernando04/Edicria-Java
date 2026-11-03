package com.qqd.edicria.entities.enums;

import lombok.Getter;

@Getter
public enum EnumGeneroPessoa {

    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    OUTRO("Outro");

    private String descricao;

    EnumGeneroPessoa(String descricao) {
        this.descricao = descricao;
    }
}

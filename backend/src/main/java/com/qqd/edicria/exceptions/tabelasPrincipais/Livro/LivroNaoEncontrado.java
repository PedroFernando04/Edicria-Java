package com.qqd.edicria.exceptions.tabelasPrincipais.Livro;

public class LivroNaoEncontrado extends RuntimeException {
    public LivroNaoEncontrado(String message) {
        super(message);
    }
}

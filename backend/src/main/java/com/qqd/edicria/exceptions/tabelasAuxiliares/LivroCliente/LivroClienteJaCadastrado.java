package com.qqd.edicria.exceptions.tabelasAuxiliares.LivroCliente;

public class LivroClienteJaCadastrado extends RuntimeException {
    public LivroClienteJaCadastrado(String message) {
        super(message);
    }
}

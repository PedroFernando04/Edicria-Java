package com.qqd.edicria.services.tabelasAuxiliares;

import com.qqd.edicria.mappers.tabelasAuxiliares.LivroClienteMapper;
import com.qqd.edicria.repositories.tabelasAuxiliares.LivroClienteRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LivroClienteServiceTest {

    @Mock
    private LivroClienteRepository livroClienteRepository;
    @Mock
    private LivroClienteMapper livroClienteMapper;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private  LivroRepository livroRepository;

    @InjectMocks
    private LivroClienteService livroClienteService;

}

package com.qqd.edicria.services.tabelasAuxiliares;

import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    public void deveEncontrarUsuarioComSucesso(){

        Usuario usuario = new Usuario();

        usuario.setSenha("senhaCriptografada");
        usuario.setEmail("pedro@email.com");

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado =
                usuarioDetailsService.loadUserByUsername("pedro@email.com");

        assertNotNull(resultado);
        assertEquals("pedro@email.com", resultado.getUsername());
        assertEquals("senhaCriptografada", resultado.getPassword());

        verify(usuarioRepository)
                .findByEmail("pedro@email.com");

    }

    @Test
    public void deveLancarExcecaoParaUsarioNaoEncontrado(){
        Usuario usuario = new Usuario();

        usuario.setEmail("pedro@email.com");
        usuario.setSenha("senhaCriptografada");

        when(usuarioRepository.findByEmail("naoCadastrado@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("naoCadastrado@email.com")
        );

        verify(usuarioRepository)
                .findByEmail("naoCadastrado@email.com");
    }
}

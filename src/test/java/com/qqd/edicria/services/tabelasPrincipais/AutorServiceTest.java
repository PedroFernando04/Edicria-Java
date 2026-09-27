package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;
import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorJaCadastrado;
import com.qqd.edicria.mappers.tabelasPrincipais.AutorMapper;
import com.qqd.edicria.repositories.tabelasPrincipais.AutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private AutorMapper autorMapper;

    @InjectMocks
    private AutorService autorService;

    // createAutor
    @Test
    void deveCadastrarAutorComSucesso(){

        AutorRequestDTO dto = new AutorRequestDTO(
                "Douglas Adams",
                EnumPaises.REINO_UNIDO,
                EnumGeneroPessoa.MASCULINO
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");
        autor.setPaisOrigem(EnumPaises.REINO_UNIDO);
        autor.setGenero(EnumGeneroPessoa.MASCULINO);

        AutorResponseDTO responseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                EnumPaises.REINO_UNIDO,
                EnumGeneroPessoa.MASCULINO
        );

        when(autorRepository.existsByNome("Douglas Adams"))
                .thenReturn(false);

        when(autorMapper.toEntity(dto))
                .thenReturn(autor);

        when(autorMapper.toResponseDTO(autor))
                .thenReturn(responseDTO);

        AutorResponseDTO resultado =
                autorService.createAutor(dto);

        assertNotNull(resultado);

        assertEquals("Douglas Adams", resultado.nome());
        assertEquals(EnumPaises.REINO_UNIDO, resultado.paisOrigem());
        assertEquals(EnumGeneroPessoa.MASCULINO, resultado.genero());

        verify(autorRepository).save(autor);
    }

    @Test
    void deveLancarExcecaoAutorJaCadastrado(){
        AutorRequestDTO dto = new AutorRequestDTO(
                "Douglas Adams",
                null,
                null
        );

        when(autorRepository.existsByNome("Douglas Adams"))
                .thenReturn(true);

        assertThrows(
                AutorJaCadastrado.class,
                () -> autorService.createAutor(dto)
        );


    }

    @Test
    void naoDeveCadastrarAutorComNomeJaCadastrado(){
        AutorRequestDTO dto = new AutorRequestDTO(
                "Douglas Adams",
                null,
                null
        );

        when(autorRepository.existsByNome("Douglas Adams"))
                .thenReturn(true);

        try {
            autorService.createAutor(dto);
        } catch (AutorJaCadastrado ignored) {}

        verify(autorRepository, never()).save(any());
        verify(autorMapper, never()).toEntity(dto);
    }

    //updateAutor
    @Test
    void deveAlterarAutorComSucesso(){

        Autor autor = new Autor();
        autor.setNome("Clarisse Lispector");
        autor.setPaisOrigem(EnumPaises.UCRANIA);
        autor.setGenero(EnumGeneroPessoa.FEMININO);

        Long id = 1L;

        AutorUpdateRequestDTO dto = new AutorUpdateRequestDTO(
                "Clarice Lispector",
                null,
                null
        );

        AutorResponseDTO responseDTO = new AutorResponseDTO(
                1L,
                "Clarice Lispector",
                EnumPaises.UCRANIA,
                EnumGeneroPessoa.FEMININO
        );

        when(autorRepository.findById(id))
                .thenReturn(Optional.of(autor));

        when(autorRepository.existsByNome(dto.nome()))
                .thenReturn(false);

        when(autorMapper.toResponseDTO(autor))
                .thenReturn(responseDTO);

        AutorResponseDTO resultado =
                autorService.updateAutor(dto, id);

        assertNotNull(resultado);

        assertEquals("Clarice Lispector", autor.getNome());
        assertEquals(EnumPaises.UCRANIA, autor.getPaisOrigem());
        assertEquals(EnumGeneroPessoa.FEMININO, autor.getGenero());

        verify(autorRepository).save(autor);
        verify(autorMapper).toResponseDTO(autor);
    }

}

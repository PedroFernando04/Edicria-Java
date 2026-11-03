package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;
import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraNaoEncontrada;
import com.qqd.edicria.exceptions.tabelasPrincipais.Livro.TituloJaCadastrado;
import com.qqd.edicria.mappers.tabelasPrincipais.AutorMapper;
import com.qqd.edicria.mappers.tabelasPrincipais.EditoraMapper;
import com.qqd.edicria.mappers.tabelasPrincipais.LivroMapper;
import com.qqd.edicria.repositories.tabelasPrincipais.AutorRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.EditoraRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.qqd.edicria.entities.enums.EnumGeneroPessoa.MASCULINO;
import static com.qqd.edicria.entities.enums.EnumPaises.BRASIL;
import static com.qqd.edicria.entities.enums.EnumPaises.REINO_UNIDO;
import static com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro.LIVRO;
import static com.qqd.edicria.entities.enums.livros.EnumFormatoLivro.FISICO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;
    @Mock
    private LivroMapper livroMapper;
    @Mock
    private AutorRepository autorRepository;
    @Mock
    private EditoraRepository editoraRepository;
    @Mock
    private AutorMapper autorMapper;
    @Mock
    private EditoraMapper editoraMapper;

    @InjectMocks
    private LivroService livroService;

    //createLivro
    @Test
    void deveCadastrarLivroComSucesso(){
        LivroRequestDTO dto = new LivroRequestDTO(
                "O Guia do Mochileiro das Galáxias",
                "Douglas Adams",
                "Sextante",
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );

        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Douglas Adams");

        AutorResponseDTO autorResponseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                REINO_UNIDO,
                MASCULINO
        );

        Editora editora = new Editora();
        editora.setId(1L);
        editora.setNome("Sextante");

        EditoraResponseDTO editoraResponseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                BRASIL
        );

        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("O Guia do Mochileiro das Galáxias");
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(LocalDate.of(2004,4,10));
        livro.setCategoria(LIVRO);
        livro.setFormato(FISICO);

        LivroResponseDTO responseDTO = new LivroResponseDTO(
                1L,
                "O Guia do Mochileiro das Galáxias",
                autorResponseDTO,
                editoraResponseDTO,
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );

        when(livroRepository.existsByTitulo(dto.titulo()))
                .thenReturn(false);

        when(autorRepository.findByNome(dto.autor()))
                .thenReturn(Optional.of(autor));
        when(editoraRepository.findByNome(dto.editora()))
                .thenReturn(Optional.of(editora));

        when(livroMapper.toEntity(dto, autor, editora))
                .thenReturn(livro);

        when(livroMapper.toResponseDTO(livro))
                .thenReturn(responseDTO);

        LivroResponseDTO resultado
                = livroService.createLivro(dto);


        assertNotNull(resultado);

        assertEquals("O Guia do Mochileiro das Galáxias", resultado.titulo());
        assertEquals("Douglas Adams", resultado.autor().nome());
        assertEquals("Sextante", resultado.editora().nome());
        assertEquals(LocalDate.of(2004,4,10), resultado.dataLancamento());
        assertEquals(LIVRO, resultado.categoria());
        assertEquals(FISICO, resultado.formato());

        verify(livroRepository).save(livro);
    }

    @Test
    void naoDeveCadastrarLivroComTituloJaCadastradoAndDeveLancarExceptionTituloJaCadastrado(){
        LivroRequestDTO dto = new LivroRequestDTO(
                "O Guia do Mochileiro das Galáxias",
                "Douglas Adams",
                "Sextante",
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );

        when(livroRepository.existsByTitulo(dto.titulo()))
                .thenReturn(true);


        assertThrows(
                TituloJaCadastrado.class,
                () -> livroService.createLivro(dto)
        );

        verify(livroRepository, never()).save(any());

    }

    @Test
    void naoDeveCadastrarLivroComAutorNaoEncontradoAndDeveLancarExceptionAutorNaoEncontrado(){
        LivroRequestDTO dto = new LivroRequestDTO(
                "O Guia do Mochileiro das Galáxias",
                "Douglas Adams",
                "Sextante",
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );


        when(livroRepository.existsByTitulo(dto.titulo())).thenReturn(false);

        when(autorRepository.findByNome(dto.autor())).thenReturn(Optional.empty());

        assertThrows(
                AutorNaoEncontrado.class,
                () -> livroService.createLivro(dto)
        );


        verify(livroRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarLivroComEditoraNaoEncontradaAndDeveLancarExceptionEditoraNaoEncontrado(){
        LivroRequestDTO dto = new LivroRequestDTO(
                "O Guia do Mochileiro das Galáxias",
                "Douglas Adams",
                "Sextante",
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");


        when(livroRepository.existsByTitulo(dto.titulo())).thenReturn(false);

        when(autorRepository.findByNome(dto.autor())).thenReturn(Optional.of(autor));

        when(editoraRepository.findByNome(dto.editora())).thenReturn(Optional.empty());


        assertThrows(
                EditoraNaoEncontrada.class,
                () -> livroService.createLivro(dto)
        );

        verify(livroRepository, never()).save(any());
    }

    //updateLivro
    @Test
    void deveAlterarLivroComSucesso(){

        Long id = 1L;

        LivroUpdateRequestDTO dto = new LivroUpdateRequestDTO(
                "O Guia do Mochileiro das Galáxias", //Deve ser alterado
                "Douglas Adams", //DEve ser ignorado pois é igual
                " ", //deve ser ignorado pq é blank
                null, //deve ser ignoradp pq é null
                null, //deve ser ignoradp pq é null
                FISICO //deve ser ignoradp pq é igual
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");
        AutorResponseDTO autorResponseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                REINO_UNIDO,
                MASCULINO
        );

        Editora editora = new Editora();
        editora.setNome("Sextante");
        EditoraResponseDTO editoraResponseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                BRASIL
        );

        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Guia do Mochileiro");
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(LocalDate.of(2004,4,10));
        livro.setCategoria(LIVRO);
        livro.setFormato(FISICO);

        LivroResponseDTO responseDTO = new LivroResponseDTO(
                1L,
                "O Guia do Mochileiro das Galáxias",
                autorResponseDTO,
                editoraResponseDTO,
                LocalDate.of(2004,4,10),
                LIVRO,
                FISICO
        );

        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));
        when(autorRepository.findByNome(dto.autor())).thenReturn(Optional.of(autor));
        when(editoraRepository.findByNome(dto.editora())).thenReturn(Optional.of(editora));

        when(livroRepository.existsByTitulo(dto.titulo())).thenReturn(false);

        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);

        LivroResponseDTO resultado = livroService.updateLivro(dto, id);

        assertNotNull(resultado);

        assertEquals("O Guia do Mochileiro das Galáxias", livro.getTitulo());
        assertEquals("Douglas Adams", livro.getAutor().getNome());
        assertEquals("Sextante", livro.getEditora().getNome());
        assertEquals(LocalDate.of(2004,4,10), livro.getDataLancamento());
        assertEquals(LIVRO, livro.getCategoria());
        assertEquals(FISICO, livro.getFormato());

        verify(livroRepository).save(livro);
    }


}

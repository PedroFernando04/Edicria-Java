package com.qqd.edicria.services.tabelasAuxiliares;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente.LivroClienteRequestDTO;
import com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente.LivroClienteUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasAuxiliares.LivroClienteResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;
import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumLingua;
import com.qqd.edicria.entities.enums.EnumPaises;
import com.qqd.edicria.entities.enums.livros.EnumCategoriasLivro;
import com.qqd.edicria.entities.enums.livros.EnumFormatoLivro;
import com.qqd.edicria.entities.enums.livros.EnumStatusLivro;
import com.qqd.edicria.entities.tabelasAuxiliares.LivroCliente;
import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import com.qqd.edicria.exceptions.tabelasAuxiliares.LivroCliente.LivroClienteJaCadastrado;
import com.qqd.edicria.mappers.tabelasAuxiliares.LivroClienteMapper;
import com.qqd.edicria.repositories.tabelasAuxiliares.LivroClienteRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    //createLivroCliente
    @Test
    public void deveCadastrarLivroClienteComSucesso(){
        LivroClienteRequestDTO dto = new LivroClienteRequestDTO(
                "pedro@email.com",
                "O Guia do Mochileiro das Galáxias",
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.5"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");
        usuario.setSenha("1234");
        usuario.setGenero(EnumGeneroPessoa.MASCULINO);
        usuario.setPaisOrigem(EnumPaises.BRASIL);
        usuario.setDataNascimento(LocalDate.of(2004, 4, 28));

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro",
                "pedro@email.com",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2004, 4, 28),
                false
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");
        autor.setPaisOrigem(EnumPaises.REINO_UNIDO);
        autor.setGenero(EnumGeneroPessoa.MASCULINO);

        AutorResponseDTO autorResponseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                EnumPaises.REINO_UNIDO,
                EnumGeneroPessoa.MASCULINO
        );

        Editora editora = new Editora();
        editora.setNome("Sextante");
        editora.setPaisOrigem(EnumPaises.BRASIL);

        EditoraResponseDTO editoraResponseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                EnumPaises.BRASIL
        );

        Livro livro = new Livro();
        livro.setTitulo("O Guia do Mochileiro das Galáxias");
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(LocalDate.of(2004,4,10));
        livro.setCategoria(EnumCategoriasLivro.LIVRO);
        livro.setFormato(EnumFormatoLivro.FISICO);

        LivroResponseDTO livroResponseDTO = new LivroResponseDTO(
                1L,
                "O Guia do Mochileiro das Galáxias",
                autorResponseDTO,
                editoraResponseDTO,
                LocalDate.of(2004,4,10),
                EnumCategoriasLivro.LIVRO,
                EnumFormatoLivro.FISICO
        );


        LivroCliente livroCliente = new LivroCliente();
        livroCliente.setId(1L);
        livroCliente.setUsuario(usuario);
        livroCliente.setLivro(livro);
        livroCliente.setStatus(EnumStatusLivro.LIDO);
        livroCliente.setDataInicioLeitura(LocalDate.of(2026, 8, 9));
        livroCliente.setDataTerminoLeitura(LocalDate.of(2026, 10, 1));
        livroCliente.setNota(new BigDecimal("8.5"));
        livroCliente.setResenha("Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando");
        livroCliente.setLinguaLida(EnumLingua.PORTUGUES);

        LivroClienteResponseDTO livroClienteResponseDTO = new LivroClienteResponseDTO(
                1L,
                usuarioResponseDTO,
                livroResponseDTO,
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.5"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );


        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findByTitulo("O Guia do Mochileiro das Galáxias"))
                .thenReturn(Optional.of(livro));

        when(livroClienteRepository.existsByUsuarioAndLivro(usuario, livro))
                .thenReturn(false);

        when(livroClienteMapper.toEntity(dto, livro, usuario))
                .thenReturn(livroCliente);

        when(livroClienteMapper.toResponseDTO(livroCliente))
                .thenReturn(livroClienteResponseDTO);

        LivroClienteResponseDTO resultado =
                livroClienteService.createLivroCliente(dto);


        assertNotNull(resultado);

        assertEquals(1L, resultado.id());
        assertEquals(usuarioResponseDTO, resultado.usuario());
        assertEquals(livroResponseDTO, resultado.livro());
        assertEquals(EnumStatusLivro.LIDO, resultado.statusLivro());
        assertEquals(LocalDate.of(2026, 8, 9), resultado.dataInicioLeitura());
        assertEquals(LocalDate.of(2026, 10, 1), resultado.dataTerminoLeitura());
        assertEquals(new BigDecimal("8.5"), resultado.nota());
        assertEquals(livroCliente.getResenha(), resultado.resenha());
        assertEquals(EnumLingua.PORTUGUES, resultado.linguaLida());

        verify(livroClienteRepository).save(any());
    }

    @Test
    public void naoDeveCadastrarLivroClienteJaExistenteAndDeveLancarExcecaoLivroClienteJaCadastrado() {
        LivroClienteRequestDTO dto = new LivroClienteRequestDTO(
                "pedro@email.com",
                "O Guia do Mochileiro das Galáxias",
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.5"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");
        usuario.setSenha("1234");
        usuario.setGenero(EnumGeneroPessoa.MASCULINO);
        usuario.setPaisOrigem(EnumPaises.BRASIL);
        usuario.setDataNascimento(LocalDate.of(2004, 4, 28));

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro",
                "pedro@email.com",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2004, 4, 28),
                false
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");
        autor.setPaisOrigem(EnumPaises.REINO_UNIDO);
        autor.setGenero(EnumGeneroPessoa.MASCULINO);

        AutorResponseDTO autorResponseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                EnumPaises.REINO_UNIDO,
                EnumGeneroPessoa.MASCULINO
        );

        Editora editora = new Editora();
        editora.setNome("Sextante");
        editora.setPaisOrigem(EnumPaises.BRASIL);

        EditoraResponseDTO editoraResponseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                EnumPaises.BRASIL
        );

        Livro livro = new Livro();
        livro.setTitulo("O Guia do Mochileiro das Galáxias");
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(LocalDate.of(2004,4,10));
        livro.setCategoria(EnumCategoriasLivro.LIVRO);
        livro.setFormato(EnumFormatoLivro.FISICO);

        LivroResponseDTO livroResponseDTO = new LivroResponseDTO(
                1L,
                "O Guia do Mochileiro das Galáxias",
                autorResponseDTO,
                editoraResponseDTO,
                LocalDate.of(2004,4,10),
                EnumCategoriasLivro.LIVRO,
                EnumFormatoLivro.FISICO
        );


        LivroCliente livroCliente = new LivroCliente();
        livroCliente.setId(1L);
        livroCliente.setUsuario(usuario);
        livroCliente.setLivro(livro);
        livroCliente.setStatus(EnumStatusLivro.LIDO);
        livroCliente.setDataInicioLeitura(LocalDate.of(2026, 8, 9));
        livroCliente.setDataTerminoLeitura(LocalDate.of(2026, 10, 1));
        livroCliente.setNota(new BigDecimal("8.5"));
        livroCliente.setResenha("Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando");
        livroCliente.setLinguaLida(EnumLingua.PORTUGUES);

        LivroClienteResponseDTO livroClienteResponseDTO = new LivroClienteResponseDTO(
                1L,
                usuarioResponseDTO,
                livroResponseDTO,
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.5"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );


        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findByTitulo("O Guia do Mochileiro das Galáxias"))
                .thenReturn(Optional.of(livro));

        when(livroClienteRepository.existsByUsuarioAndLivro(usuario, livro))
                .thenReturn(true);

        assertThrows(
                LivroClienteJaCadastrado.class,
                () -> livroClienteService.createLivroCliente(dto)
        );

        verify(livroClienteRepository, never())
                .save(any(LivroCliente.class));
    }


    //updateLivroCliente
    @Test
    public void deveAlterarLivroClienteComSucesso(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");
        usuario.setSenha("1234");
        usuario.setGenero(EnumGeneroPessoa.MASCULINO);
        usuario.setPaisOrigem(EnumPaises.BRASIL);
        usuario.setDataNascimento(LocalDate.of(2004, 4, 28));

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro",
                "pedro@email.com",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2004, 4, 28),
                false
        );

        Autor autor = new Autor();
        autor.setNome("Douglas Adams");
        autor.setPaisOrigem(EnumPaises.REINO_UNIDO);
        autor.setGenero(EnumGeneroPessoa.MASCULINO);

        AutorResponseDTO autorResponseDTO = new AutorResponseDTO(
                1L,
                "Douglas Adams",
                EnumPaises.REINO_UNIDO,
                EnumGeneroPessoa.MASCULINO
        );

        Editora editora = new Editora();
        editora.setNome("Sextante");
        editora.setPaisOrigem(EnumPaises.BRASIL);

        EditoraResponseDTO editoraResponseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                EnumPaises.BRASIL
        );

        Livro livro = new Livro();
        livro.setTitulo("O Guia do Mochileiro das Galáxias");
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(LocalDate.of(2004,4,10));
        livro.setCategoria(EnumCategoriasLivro.LIVRO);
        livro.setFormato(EnumFormatoLivro.FISICO);

        LivroResponseDTO livroResponseDTO = new LivroResponseDTO(
                1L,
                "O Guia do Mochileiro das Galáxias",
                autorResponseDTO,
                editoraResponseDTO,
                LocalDate.of(2004,4,10),
                EnumCategoriasLivro.LIVRO,
                EnumFormatoLivro.FISICO
        );


        LivroCliente livroCliente = new LivroCliente();
        livroCliente.setId(1L);
        livroCliente.setUsuario(usuario);
        livroCliente.setLivro(livro);
        livroCliente.setStatus(EnumStatusLivro.LIDO);
        livroCliente.setDataInicioLeitura(LocalDate.of(2026, 8, 9));
        livroCliente.setDataTerminoLeitura(LocalDate.of(2026, 10, 1));
        livroCliente.setNota(new BigDecimal("8.5"));
        livroCliente.setResenha("Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando");
        livroCliente.setLinguaLida(EnumLingua.PORTUGUES);

        LivroClienteResponseDTO livroClienteResponseDTO = new LivroClienteResponseDTO(
                1L,
                usuarioResponseDTO,
                livroResponseDTO,
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.5"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );

        LivroClienteUpdateRequestDTO dto = new LivroClienteUpdateRequestDTO(
                "pedro@email.com",
                "O Guia do Mochileiro das Galáxias",
                EnumStatusLivro.LIDO,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("8.42"),
                "Livro curto e divertido, tem algumas piadas muito específicas que são hilárias e inúmeros conceitos muito interessantes e intrigantes pela curiosidade. No fim é uma história muito simples que foca nessa coisa dos 1000 conceitos e piadas que o autor joga em você, tem uma porrada que é só tosca e sem nexo (parecendo que ele só saiu batendo a cabeça no teclado e inventando palavras) o que tira um pouco da graça, mas como o livro é dinâmico não chega a entediar; mas como é basicamente essa repetição, eu diria que em quantidade ele mais erra do que acerta, mas em qualidade quando acerta pega em cheio. Todos os personagens são muito carismáticos. Certamente irei ler a sequência, só não sei quando",
                EnumLingua.PORTUGUES
        );

        when(livroClienteRepository.findByLivroIdAndUsuarioId(livro.getId(), usuario.getId()))
                .thenReturn(Optional.of(livroCliente));

        when(livroClienteMapper.toResponseDTO(livroCliente)).thenReturn(livroClienteResponseDTO);

        LivroClienteResponseDTO resultado =
                livroClienteService.updateLivroCliente(
                        dto,
                        livro.getId(),
                        usuario.getId()
                );

        assertNotNull(resultado);

        assertEquals(new BigDecimal("8.42"), livroCliente.getNota());

        verify(livroClienteRepository).save(livroCliente);

        verify(usuarioRepository, never()).findByEmail("pedro@email.com");
        verify(livroRepository, never()).findByTitulo("O Guia do Mochileiro das Galáxias");
    }


}

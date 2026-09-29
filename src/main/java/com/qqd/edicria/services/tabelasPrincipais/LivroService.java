package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;

import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;

import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraNaoEncontrada;
import com.qqd.edicria.exceptions.tabelasPrincipais.Livro.LivroNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Livro.TituloJaCadastrado;

import com.qqd.edicria.mappers.tabelasPrincipais.AutorMapper;
import com.qqd.edicria.mappers.tabelasPrincipais.EditoraMapper;
import com.qqd.edicria.mappers.tabelasPrincipais.LivroMapper;

import com.qqd.edicria.repositories.tabelasPrincipais.AutorRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.EditoraRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroMapper livroMapper;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;
    private final AutorMapper autorMapper;
    private final EditoraMapper editoraMapper;

    public LivroService(LivroRepository livroRepository,
                        LivroMapper livroMapper,
                        AutorRepository autorRepository,
                        EditoraRepository editoraRepository, AutorMapper autorMapper, EditoraMapper editoraMapper)
    {
        this.livroRepository = livroRepository;
        this.livroMapper = livroMapper;
        this.autorRepository = autorRepository;
        this.editoraRepository = editoraRepository;
        this.autorMapper = autorMapper;
        this.editoraMapper = editoraMapper;
    }

    public LivroResponseDTO createLivro(LivroRequestDTO dto){

        if(livroRepository.existsByTitulo(dto.titulo())){
            throw new TituloJaCadastrado("Título já cadastarado: " + dto.titulo());
        }

        Autor autor = autorRepository.findByNome(dto.autor())
                .orElseThrow(() ->
                        new AutorNaoEncontrado("Autor não encontrado: " +  dto.autor()));

        Editora editora = editoraRepository.findByNome(dto.editora())
                .orElseThrow(() ->
                        new EditoraNaoEncontrada("Editora não encontrada:  " +  dto.editora()));

        Livro livro = livroMapper.toEntity(dto, autor, editora);
        livroRepository.save(livro);

        return livroMapper.toResponseDTO(livro);
    }

    public LivroResponseDTO updateLivro(LivroUpdateRequestDTO dto, Long id){
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() ->
                        new LivroNaoEncontrado("Livro não encontrado")
                );

        if (livroRepository.existsByTitulo(dto.titulo())){
            throw new TituloJaCadastrado("Título já cadastarado: " + dto.titulo());
        }

        boolean alterou = false;

        if(dto.titulo() != null
                && !dto.titulo().equals(livro.getTitulo())
                && !dto.titulo().isBlank()){
            livro.setTitulo(dto.titulo());
            alterou = true;
        }

        if(dto.autor() != null
                && !dto.autor().equals(livro.getAutor().toString())
                && !dto.autor().isBlank()){

            Autor autor = autorRepository.findByNome(dto.autor())
                    .orElseThrow(() ->
                            new AutorNaoEncontrado("Autor não encontrado: " +  dto.autor())
                    );

            livro.setAutor(autor);
            alterou = true;
        }
        if(dto.editora() != null
                && !dto.editora().equals(livro.getEditora().toString())
                && !dto.editora().isBlank()){

            Editora editora = editoraRepository.findByNome(dto.editora())
                    .orElseThrow(() ->
                            new EditoraNaoEncontrada("Editora não encontrada: " +  dto.editora())
                    );

            livro.setEditora(editora);
            alterou = true;
        }
        if(dto.dataLancamento() != null
                && !dto.dataLancamento().equals(livro.getDataLancamento())){
            livro.setDataLancamento(dto.dataLancamento());
            alterou = true;
        }
        if(dto.categoria() != null
                && !dto.categoria().equals(livro.getCategoria())){
            livro.setCategoria(dto.categoria());
            alterou = true;
        }
        if(dto.formato() != null
                && !dto.formato().equals(livro.getFormato())){
            livro.setFormato(dto.formato());
            alterou = true;
        }

        if (alterou) {
            livroRepository.save(livro);
        }

        return livroMapper.toResponseDTO(livro);
    }

    public LivroResponseDTO getLivro(Long id){
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() ->
                        new LivroNaoEncontrado("Livro não encontrado")
                );

        return livroMapper.toResponseDTO(livro);
    }

    public List<LivroResponseDTO> getAllLivros(){
        List<Livro> livros = livroRepository.findAll();

        return livros.stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getTitulo(),
                        autorMapper.toResponseDTO(livro.getAutor()),
                        editoraMapper.toResponseDTO(livro.getEditora()),
                        livro.getDataLancamento(),
                        livro.getCategoria(),
                        livro.getFormato()
                        )
                ).toList();
    }
}

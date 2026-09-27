package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;

import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;

import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraNaoEncontrada;
import com.qqd.edicria.exceptions.tabelasPrincipais.Livro.TituloJaCadastrado;

import com.qqd.edicria.mappers.tabelasPrincipais.LivroMapper;

import com.qqd.edicria.repositories.tabelasPrincipais.AutorRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.EditoraRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;

import org.springframework.stereotype.Service;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroMapper livroMapper;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;

    public LivroService(LivroRepository livroRepository,
                        LivroMapper livroMapper,
                        AutorRepository autorRepository,
                        EditoraRepository editoraRepository)
    {
        this.livroRepository = livroRepository;
        this.livroMapper = livroMapper;
        this.autorRepository = autorRepository;
        this.editoraRepository = editoraRepository;
    }

    public LivroResponseDTO createLivro(LivroRequestDTO dto){

        if(livroRepository.existsByTitulo(dto.titulo())){
            throw new TituloJaCadastrado("Título já cadastarado: " + dto.titulo());
        }

        Autor autor = autorRepository.findByNome(dto.autor())
                .orElseThrow(() ->
                        new AutorNaoEncontrado("Autor não encontrado: " +  dto.autor()));

        Editora editora = editoraRepository.findByNome(dto.autor())
                .orElseThrow(() ->
                        new EditoraNaoEncontrada("Editora não encontrada:  " +  dto.editora()));

        Livro livro = livroMapper.toEntity(dto, autor, editora);

        livroRepository.save(livro);

        return livroMapper.toResponseDTO(livro);
    }
}

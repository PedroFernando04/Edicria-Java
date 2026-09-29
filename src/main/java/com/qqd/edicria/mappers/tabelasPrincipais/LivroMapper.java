package com.qqd.edicria.mappers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;
import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import org.springframework.stereotype.Component;

@Component
public class LivroMapper {

    private final AutorMapper autorMapper;
    private final EditoraMapper editoraMapper;

    public  LivroMapper(AutorMapper autorMapper, EditoraMapper editoraMapper) {
        this.autorMapper = autorMapper;
        this.editoraMapper = editoraMapper;
    }

    public LivroResponseDTO toResponseDTO(Livro livro) {
        return new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                autorMapper.toResponseDTO(livro.getAutor()),
                editoraMapper.toResponseDTO(livro.getEditora()),
                livro.getDataLancamento(),
                livro.getCategoria(),
                livro.getFormato()
        );
    }

    public Livro toEntity(
            LivroRequestDTO dto,
            Autor autor,
            Editora editora) {
    Livro livro = new Livro();

        livro.setTitulo(dto.titulo());
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setDataLancamento(dto.dataLancamento());
        livro.setCategoria(dto.categoria());
        livro.setFormato(dto.formato());

        return livro;
    }
}

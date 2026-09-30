package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorUpdateRequestDTO;

import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;

import com.qqd.edicria.entities.tabelasPrincipais.Autor;

import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorJaCadastrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Autor.AutorNaoEncontrado;

import com.qqd.edicria.mappers.tabelasPrincipais.AutorMapper;

import com.qqd.edicria.repositories.tabelasPrincipais.AutorRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

    public AutorService(
            AutorRepository autorRepository,
            AutorMapper autorMapper) {

        this.autorRepository = autorRepository;
        this.autorMapper = autorMapper;
    }

    public AutorResponseDTO createAutor(AutorRequestDTO dto){

        if(autorRepository.existsByNome(dto.nome())){
            throw new AutorJaCadastrado("Autor já cadastrado: " +  dto.nome());
        }

        Autor autor = autorMapper.toEntity(dto);
        autorRepository.save(autor);

        return autorMapper.toResponseDTO(autor);
    }

    public AutorResponseDTO updateAutor(AutorUpdateRequestDTO dto, Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() ->
                        new AutorNaoEncontrado("Autor não encontrado")
                );

        boolean alterou = false;

        if(dto.nome() != null
                && !autor.getNome().equals(dto.nome())
                && !dto.nome().isBlank()){

            if(autorRepository.existsByNome(dto.nome())){
                throw new AutorJaCadastrado("Autor ja cadastrado: " +  dto.nome());
            }

            autor.setNome(dto.nome());
            alterou = true;
        }

        if(dto.paisOrigem() != null
                && !autor.getPaisOrigem().equals(dto.paisOrigem())){

            autor.setPaisOrigem(dto.paisOrigem());
            alterou = true;
        }

        if(dto.genero() != null
                && !autor.getGenero().equals(dto.genero())){

            autor.setGenero(dto.genero());
            alterou = true;
        }

        if(alterou){
            autorRepository.save(autor);
        }

        return autorMapper.toResponseDTO(autor);
    }

    public AutorResponseDTO getAutor(Long id){
        Autor autor = autorRepository
                .findById(id).orElseThrow(() ->
                        new AutorNaoEncontrado("Autor não encontrado")
                );

        return autorMapper.toResponseDTO(autor);
    }

    public List<AutorResponseDTO> getAllAutores(){
        List<Autor> autores = autorRepository.findAll();

        return autores
                .stream()
                .map(autorMapper::toResponseDTO)
                .toList();
    }
}




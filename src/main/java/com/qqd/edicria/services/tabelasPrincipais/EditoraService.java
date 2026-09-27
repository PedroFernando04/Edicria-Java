package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraUpdateRequestDTO;

import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;

import com.qqd.edicria.entities.tabelasPrincipais.Editora;

import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraJaCadastrada;
import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraNaoEncontrada;

import com.qqd.edicria.mappers.tabelasPrincipais.EditoraMapper;

import com.qqd.edicria.repositories.tabelasPrincipais.EditoraRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditoraService {

    private final EditoraRepository editoraRepository;
    private final EditoraMapper editoraMapper;

    public EditoraService(
            EditoraRepository editoraRepository,
            EditoraMapper editoraMapper)
    {
        this.editoraRepository = editoraRepository;
        this.editoraMapper = editoraMapper;
    }

    public EditoraResponseDTO createEditora (EditoraRequestDTO dto){
        if(editoraRepository.existsByNome(dto.nome())){
            throw new EditoraJaCadastrada("Editora já cadastrada: " + dto.nome());
        }

        Editora editora = editoraMapper.toEntity(dto);
        editoraRepository.save(editora);

        return  editoraMapper.toResponseDTO(editora);
    }

    public EditoraResponseDTO updateEditora (EditoraUpdateRequestDTO dto, Long id){

        Editora editora = editoraRepository.findById(id)
                .orElseThrow(() ->
                        new EditoraNaoEncontrada("Editora não encontrada")
                );

        boolean alterou = false;

        if(dto.nome() != null
                && !dto.nome().equals(editora.getNome())
                && !dto.nome().isBlank()){

            if(editoraRepository.existsByNome(dto.nome())){
                throw new EditoraJaCadastrada("Editora já cadastrada: " + editora.getNome());
            }

            editora.setNome(dto.nome());
            alterou = true;
        }

        if(dto.paisOrigem() != null
                && !dto.paisOrigem().equals(editora.getPaisOrigem())){

            editora.setPaisOrigem(dto.paisOrigem());
            alterou = true;
        }

        if(alterou){
            editoraRepository.save(editora);
        }

        return  editoraMapper.toResponseDTO(editora);
    }

    public EditoraResponseDTO getEditora (Long id){
        Editora editora = editoraRepository.findById(id)
                .orElseThrow(() ->
                        new EditoraNaoEncontrada("Editora inexistente")
                );

        return  editoraMapper.toResponseDTO(editora);
    }

    public List<EditoraResponseDTO> getAllEditoras(){
        List<Editora> editoras = editoraRepository.findAll();

        return editoras.stream().map(
                editora -> new EditoraResponseDTO(
                        editora.getId(),
                        editora.getNome(),
                        editora.getPaisOrigem()
                )).toList();
    }
}

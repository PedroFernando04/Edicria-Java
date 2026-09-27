package com.qqd.edicria.mappers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import org.springframework.stereotype.Component;

@Component
public class EditoraMapper {

    public EditoraResponseDTO toResponseDTO(Editora editora) {
        return new EditoraResponseDTO(
                editora.getId(),
                editora.getNome(),
                editora.getPaisOrigem()
        );
    }

    public Editora toEntity(EditoraRequestDTO dto) {
        Editora editora = new Editora();

        editora.setNome(dto.nome());
        editora.setPaisOrigem(dto.paisOrigem());

        return editora;
    }
}



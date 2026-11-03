package com.qqd.edicria.mappers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;
import com.qqd.edicria.entities.tabelasPrincipais.Autor;
import org.springframework.stereotype.Component;

@Component
public class AutorMapper {

    public AutorResponseDTO toResponseDTO (Autor autor){
        return new AutorResponseDTO(
                autor.getId(),
                autor.getNome(),
                autor.getPaisOrigem(),
                autor.getGenero()
        );
    }

    public Autor toEntity(AutorRequestDTO dto){
        Autor autor = new Autor();

        autor.setNome(dto.nome());
        autor.setPaisOrigem(dto.paisOrigem());
        autor.setGenero(dto.genero());

        return autor;
    }
}

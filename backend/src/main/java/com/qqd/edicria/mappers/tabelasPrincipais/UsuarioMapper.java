package com.qqd.edicria.mappers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getGenero(),
                usuario.getPaisOrigem(),
                usuario.getDataNascimento(),
                usuario.getAdm()
        );
    }

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setGenero(dto.genero());
        usuario.setPaisOrigem(dto.pais());
        usuario.setDataNascimento(dto.dataNascimento());

        return usuario;
    }
}

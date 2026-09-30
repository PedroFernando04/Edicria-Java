package com.qqd.edicria.mappers.tabelasAuxiliares;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente.LivroClienteRequestDTO;
import com.qqd.edicria.dtos.response.tabelasAuxiliares.LivroClienteResponseDTO;
import com.qqd.edicria.entities.tabelasAuxiliares.LivroCliente;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import com.qqd.edicria.mappers.tabelasPrincipais.LivroMapper;
import com.qqd.edicria.mappers.tabelasPrincipais.UsuarioMapper;
import org.springframework.stereotype.Component;

@Component
public class LivroClienteMapper {

    private final UsuarioMapper usuarioMapper;
    private final LivroMapper livroMapper;

    public LivroClienteMapper(UsuarioMapper usuarioMapper,  LivroMapper livroMapper) {
        this.usuarioMapper = usuarioMapper;
        this.livroMapper = livroMapper;
    }

    public LivroClienteResponseDTO toResponseDTO(LivroCliente livroCliente) {
        return new LivroClienteResponseDTO(
                livroCliente.getId(),
                usuarioMapper.toResponseDTO(livroCliente.getUsuario()),
                livroMapper.toResponseDTO(livroCliente.getLivro()),
                livroCliente.getStatus(),
                livroCliente.getDataInicioLeitura(),
                livroCliente.getDataTerminoLeitura(),
                livroCliente.getNota(),
                livroCliente.getResenha(),
                livroCliente.getLinguaLida()
        );
    }

    public LivroCliente toEntity(LivroClienteRequestDTO dto, Livro livro, Usuario usuario) {
        LivroCliente livroCliente = new LivroCliente();

        livroCliente.setUsuario(usuario);
        livroCliente.setLivro(livro);
        livroCliente.setStatus(dto.statusLivro());
        livroCliente.setDataInicioLeitura(dto.dataInicioLeitura());
        livroCliente.setDataTerminoLeitura(dto.dataTerminoLeitura());
        livroCliente.setNota(dto.nota());
        livroCliente.setResenha(dto.resenha());
        livroCliente.setLinguaLida(dto.linguaLida());

        return livroCliente;
    }
}

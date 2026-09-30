package com.qqd.edicria.services.tabelasAuxiliares;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente.LivroClienteRequestDTO;
import com.qqd.edicria.dtos.response.tabelasAuxiliares.LivroClienteResponseDTO;
import com.qqd.edicria.entities.tabelasAuxiliares.LivroCliente;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import com.qqd.edicria.exceptions.tabelasAuxiliares.LivroCliente.LivroClienteJaCadastrado;
import com.qqd.edicria.exceptions.tabelasAuxiliares.LivroCliente.LivroClienteNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasAuxiliares.LivroCliente.NotaInvalida;
import com.qqd.edicria.exceptions.tabelasPrincipais.Livro.LivroNaoEncontrado;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.UsuarioNaoEncontrado;
import com.qqd.edicria.mappers.tabelasAuxiliares.LivroClienteMapper;
import com.qqd.edicria.repositories.tabelasAuxiliares.LivroClienteRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.LivroRepository;
import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LivroClienteService {

    private final LivroClienteRepository livroClienteRepository;
    private final LivroClienteMapper livroClienteMapper;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public LivroClienteService(
            LivroClienteRepository livroClienteRepository,
            LivroClienteMapper livroClienteMapper,
            UsuarioRepository usuarioRepository,
            LivroRepository livroRepository
            )
    {
        this.livroClienteRepository = livroClienteRepository;
        this.livroClienteMapper = livroClienteMapper;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    public LivroClienteResponseDTO createLivroCliente(LivroClienteRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(dto.usuarioEmail())
                .orElseThrow(() ->
                        new UsuarioNaoEncontrado("Usuário não encontrado: " +  dto.usuarioEmail())
                );

        Livro livro = livroRepository.findByTitulo(dto.livro())
                .orElseThrow(() ->
                        new LivroNaoEncontrado("Livro não encontrado: " + dto.livro())
                );

        if(livroClienteRepository.existsByUsuarioAndLivro(usuario, livro)) {
            throw new LivroClienteJaCadastrado("Este livro já foi associado a este usuário");
        }

        LivroCliente livroCliente =
                livroClienteMapper.toEntity(dto, livro, usuario);

        livroClienteRepository.save(livroCliente);

        return livroClienteMapper.toResponseDTO(livroCliente);
    }

    public LivroClienteResponseDTO updateLivroCliente(LivroClienteRequestDTO dto, Long idLivro, Long idUsuario) {

        LivroCliente livroCliente = livroClienteRepository.findByLivroIdAndUsuarioId(idLivro, idUsuario)
                .orElseThrow(() ->
                        new LivroClienteNaoEncontrado(
                                "O livro " + dto.livro() + " não está associado ao usuário " + dto.usuarioEmail()
                        )
                );

        boolean alterou = false;

        if(dto.usuarioEmail() != null
                && !dto.usuarioEmail().equals(livroCliente.getUsuario().getEmail())
                && !dto.usuarioEmail().isBlank()
        ){
            Usuario usuario = usuarioRepository.findByEmail(dto.usuarioEmail())
                    .orElseThrow(() ->
                            new UsuarioNaoEncontrado(
                                    "Usuário não encontrado: " +  dto.usuarioEmail()
                            )
                    );

            livroCliente.setUsuario(usuario);
            alterou = true;
        }

        if(dto.livro() != null
                && !dto.livro().equals(livroCliente.getLivro().getTitulo())
                && !dto.livro().isBlank()
        ){
            Livro livro = livroRepository.findByTitulo(dto.livro())
                    .orElseThrow(() ->
                            new LivroNaoEncontrado(
                                    "Livro não encontrado: " + dto.livro()
                            )
                    );

            livroCliente.setLivro(livro);
            alterou = true;
        }

        if(dto.statusLivro() != null
                && !dto.statusLivro().equals(livroCliente.getStatus())
        ){
            livroCliente.setStatus(dto.statusLivro());
            alterou = true;
        }
        if(dto.dataInicioLeitura() != null
                && !dto.dataInicioLeitura().equals(livroCliente.getDataInicioLeitura())
        ){
            livroCliente.setDataInicioLeitura(dto.dataInicioLeitura());
            alterou = true;
        }
        if(dto.dataTerminoLeitura() != null
                && !dto.dataTerminoLeitura().equals(livroCliente.getDataTerminoLeitura())
        ){
            livroCliente.setDataTerminoLeitura(dto.dataTerminoLeitura());
            alterou = true;
        }
        if(dto.nota() != null
                && !dto.nota().equals(livroCliente.getNota())
        ){
            if(dto.nota().compareTo(new BigDecimal("0.0")) < 0
                    || dto.nota().compareTo(new BigDecimal("10.0")) < 0
            ){
                throw new NotaInvalida("A nota deve ser um valor entre 0 e 10");
            }
            livroCliente.setNota(dto.nota());
            alterou = true;
        }
        if(dto.resenha() != null
                && !dto.resenha().equals(livroCliente.getResenha())
                && !dto.resenha().isBlank()){
            livroCliente.setResenha(dto.resenha());
            alterou = true;
        }
        if(dto.linguaLida() != null
                && !dto.linguaLida().equals(livroCliente.getLinguaLida())
        ){
            livroCliente.setLinguaLida(dto.linguaLida());
            alterou = true;
        }

        if(alterou){
            livroClienteRepository.save(livroCliente);
        }

        return livroClienteMapper.toResponseDTO(livroCliente);
    }

    public Void deleteLivroCliente(Long id) {
        livroClienteRepository.deleteById(id);

        return  null;
    }

    public LivroClienteResponseDTO getLivroCliente(Long id) {
        LivroCliente livroCliente = livroClienteRepository.findById(id)
                .orElseThrow(() ->
                        new LivroClienteNaoEncontrado("Este livro não está associado ao usuário")
                );

        return livroClienteMapper.toResponseDTO(livroCliente);
    }

    public LivroClienteResponseDTO getLivroClienteByUserAndLivro(Long idUsuario,  Long idLivro) {

        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado"));

        Livro livro = livroRepository.findById(idLivro).orElseThrow(() -> new LivroNaoEncontrado("Livro não encontrado"));

        LivroCliente livroCliente = livroClienteRepository.findByUsuarioAndLivro(usuario,  livro)
                .orElseThrow(() ->
                        new LivroClienteNaoEncontrado("Este livro não está associado ao usuário")
                );

        return livroClienteMapper.toResponseDTO(livroCliente);
    }

    public List<LivroClienteResponseDTO> getAllLivroClienteByUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new UsuarioNaoEncontrado("Usuário não encontrado: ")
                );

        return livroClienteRepository.findAllByUsuario(usuario)
                .stream()
                .map(livroClienteMapper::toResponseDTO)
                .toList();
    }
}

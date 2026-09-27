package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LoginRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioUpdateRequestDTO;

import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;

import com.qqd.edicria.entities.tabelasPrincipais.Usuario;

import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.EmailJaCadastradoException;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.NomeJaCadastradoException;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.UsuarioNaoEncontrado;

import com.qqd.edicria.mappers.tabelasPrincipais.UsuarioMapper;

import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;


    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper,
                          AuthenticationManager authenticationManager) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;

        this.authenticationManager = authenticationManager;
        this.securityContextRepository =
                new HttpSessionSecurityContextRepository();
    }

    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO dto) {

        if(usuarioRepository.existsByEmail(dto.email())){
            throw new EmailJaCadastradoException("Email já cadastrado: " +   dto.email());
        }
        if(usuarioRepository.existsByNome(dto.nome())){
            throw new NomeJaCadastradoException("Nome já cadastrado: " +   dto.nome());
        }

        Usuario usuario = usuarioMapper.toEntity(dto);

        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setAdm(false);

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuario);
    }

    public List<UsuarioResponseDTO> getAllUsuarios(){
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getGenero(),
                        usuario.getPaisOrigem(),
                        usuario.getDataNascimento(),
                        usuario.getAdm()
                ))
                .toList();
    }

    public UsuarioResponseDTO getUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontrado("Usuário não encontrado")
                );

        return usuarioMapper.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO updateUsuario(UsuarioUpdateRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontrado("Usuário não encontrado: " + email)
                );

        boolean alterou = false;

        if(dto.nome() != null
                && !usuario.getNome().equals(dto.nome())
                && !dto.nome().isBlank()){

            if(usuarioRepository.existsByNome(dto.nome())){
                throw new NomeJaCadastradoException("Nome já cadastrado: " +  dto.nome());
            }

            usuario.setNome(dto.nome());
            alterou = true;
        }

        if(dto.email() != null
                && !usuario.getEmail().equals(dto.email())
                && !dto.email().isBlank()){

            if(usuarioRepository.existsByEmail(dto.email())){
                throw new EmailJaCadastradoException("Email ja cadastrado: " +  dto.email());
            }

            usuario.setEmail(dto.email());
            alterou = true;
        }

        if(dto.senha() != null
                && !passwordEncoder.matches(dto.senha(), usuario.getSenha())
                && !dto.senha().isBlank()){

            usuario.setSenha(passwordEncoder.encode(dto.senha()));
            alterou = true;
        }
        if(dto.genero() != null
                && !usuario.getGenero().equals(dto.genero())){

            usuario.setGenero(dto.genero());
            alterou = true;
        }
        if(dto.pais() != null
                && !usuario.getPaisOrigem().equals(dto.pais())){

            usuario.setPaisOrigem(dto.pais());
            alterou = true;
        }
        if(dto.dataNascimento() != null
                && !usuario.getDataNascimento().equals(dto.dataNascimento())){

            usuario.setDataNascimento(dto.dataNascimento());
            alterou = true;
        }


        if(alterou){
            usuarioRepository.save(usuario);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    public void login(
            LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response){

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.email(),
                                dto.senha()
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                request,
                response
        );
    }

    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){

        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();

        logoutHandler.logout(request, response, null);
    }
}

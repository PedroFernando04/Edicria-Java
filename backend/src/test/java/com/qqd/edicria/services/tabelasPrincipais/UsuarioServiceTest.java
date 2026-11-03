package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;
import com.qqd.edicria.entities.enums.EnumGeneroPessoa;
import com.qqd.edicria.entities.enums.EnumPaises;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.NomeJaCadastradoException;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.UsuarioNaoEncontrado;
import com.qqd.edicria.repositories.tabelasPrincipais.UsuarioRepository;
import com.qqd.edicria.mappers.tabelasPrincipais.UsuarioMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioRequestDTO;
import com.qqd.edicria.exceptions.tabelasPrincipais.Usuario.EmailJaCadastradoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    //Cadastro

    @Test
    void deveLancarExceptionQuandoEmailJaEstiverCadastrado() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Pedro Fernando",
                "pedro@email.com",
                "123456",
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> usuarioService.createUsuario(dto)
        );
    }

    @Test
    void naoDeveSalvarUsuarioQuandoEmailJaEstiverCadastrado() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Pedro Fernando",
                "pedro@email.com",
                "123456",
                null,
                null,
                null
        );

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        try{
            usuarioService.createUsuario(dto);
        } catch(EmailJaCadastradoException ignored){}

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveCriarUsuarioComSucesso() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Pedro Fernando",
                "pedro@email.com",
                "123456",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2002, 5, 15)
        );

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro Fernando");
        usuario.setEmail("pedro@email.com");
        usuario.setSenha("senha-criptografada");
        usuario.setGenero(EnumGeneroPessoa.MASCULINO);
        usuario.setPaisOrigem(EnumPaises.BRASIL);
        usuario.setDataNascimento(LocalDate.of(2002, 5, 15));
        usuario.setAdm(false);

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro Fernando",
                "pedro@email.com",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2002, 5, 15),
                false
        );

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(false);
        when(usuarioRepository.existsByNome(dto.nome()))
                .thenReturn(false);

        when(usuarioMapper.toEntity(dto))
                .thenReturn(usuario);

        when(passwordEncoder.encode(dto.senha()))
                .thenReturn("senha-criptografada");

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        UsuarioResponseDTO resultado =
                usuarioService.createUsuario(dto);

        assertNotNull(resultado);
        assertEquals("Pedro Fernando", resultado.nome());
        assertEquals("pedro@email.com", resultado.email());

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveCriptografarSenhaAntesDeSalvar(){

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Pedro Fernando",
                "pedro@email.com",
                "123456",
                EnumGeneroPessoa.MASCULINO,
                EnumPaises.BRASIL,
                LocalDate.of(2002, 5, 15)
        );

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());

        when(usuarioRepository.existsByEmail(dto.email()))
        .thenReturn(false);
        when(usuarioRepository.existsByNome(dto.nome()))
        .thenReturn(false);

        when(usuarioMapper.toEntity(dto))
        .thenReturn(usuario);

        when(passwordEncoder.encode(dto.senha()))
        .thenReturn("senha-criptografada");

        usuarioService.createUsuario(dto);

        verify(passwordEncoder).encode(dto.senha());
        assertEquals("senha-criptografada", usuario.getSenha());

    }

    //Update

    @Test
    void deveAlterarUsuarioComSucesso(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro Fernando");
        usuario.setEmail("pedro@email.com");

        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO(
                "Pedro",
                null,
                null,
                null,
                null,
                null
        );

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro",
                "pedro@email.com",
                null,
                null,
                null,
                false
        );

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        UsuarioResponseDTO resultado =
                usuarioService.updateUsuario(dto, "pedro@email.com");


        assertNotNull(resultado);
        assertEquals("Pedro", usuario.getNome());
        assertEquals("pedro@email.com", usuario.getEmail());
        assertSame(responseDTO, resultado);

        verify(usuarioRepository).save(usuario);
        verify(usuarioMapper).toResponseDTO(usuario);

    }

    @Test
    void naoDeveAlterarComponenteIgual(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");

        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO(
                "Pedro",
                null,
                null,
                null,
                null,
                null

        );

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "Pedro",
                "pedro@email.com",
                null,
                null,
                null,
                null
        );

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        UsuarioResponseDTO resultado =
                usuarioService.updateUsuario(dto, "pedro@email.com");


        assertNotNull(resultado);
        assertEquals("Pedro", usuario.getNome());

        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void naoDeveAlterarComponenteNulo(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");

        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                null,
                null,
                null,
                null,
                null,
                null

        );

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        UsuarioResponseDTO resultado =
                usuarioService.updateUsuario(dto, "pedro@email.com");

        assertNotNull(resultado);
        assertEquals("Pedro", usuario.getNome());

        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void deveLancarExceptionUsuarioNaoEncontrado(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");

        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontrado.class,
                () -> usuarioService.updateUsuario(dto, "pedro@email.com")
        );
    }

    @Test
    void naoDeveAlterarNomeParaUmJaExistente(){

        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");

        UsuarioUpdateRequestDTO dto = new UsuarioUpdateRequestDTO(
                "Fernando",
                null,
                null,
                null,
                null,
                null
        );

        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByNome("Fernando"))
                .thenReturn(true);

        assertThrows(
                NomeJaCadastradoException.class,
                () -> usuarioService.updateUsuario(dto, "pedro@email.com")
        );

        assertEquals("Pedro", usuario.getNome());

        verify(usuarioRepository, never()).save(usuario);
    }

    //GET

    @Test
    void deveTrazerTodosUsuarios(){
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Pedro");

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Jorge");

        Usuario usuario3 = new Usuario();
        usuario3.setNome("Fernando");

        when(usuarioRepository.findAll())
                .thenReturn(Arrays.asList(usuario1, usuario2, usuario3));

        List<UsuarioResponseDTO> resultado =
                usuarioService.getAllUsuarios();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals("Pedro", resultado.get(0).nome());
        assertEquals("Jorge", resultado.get(1).nome());
        assertEquals("Fernando", resultado.get(2).nome());

        verify(usuarioRepository).findAll();


    }
}
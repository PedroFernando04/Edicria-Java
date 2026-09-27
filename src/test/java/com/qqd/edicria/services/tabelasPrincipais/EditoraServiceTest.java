package com.qqd.edicria.services.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;
import com.qqd.edicria.entities.enums.EnumPaises;
import com.qqd.edicria.entities.tabelasPrincipais.Editora;
import com.qqd.edicria.exceptions.tabelasPrincipais.Editora.EditoraJaCadastrada;
import com.qqd.edicria.mappers.tabelasPrincipais.EditoraMapper;
import com.qqd.edicria.repositories.tabelasPrincipais.EditoraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditoraServiceTest {

    @Mock
    private EditoraRepository editoraRepository;
    @Mock
    private EditoraMapper editoraMapper;

    @InjectMocks
    private EditoraService editoraService;


    //createEditora
    @Test
    void deveCadastrarEditoraComSucesso(){
        EditoraRequestDTO dto =
                new EditoraRequestDTO(
                        "Sextante",
                        EnumPaises.BRASIL
                );

        Editora editora = new Editora();
        editora.setNome("Sextante");
        editora.setPaisOrigem(EnumPaises.BRASIL);

        EditoraResponseDTO responseDTO =
                new EditoraResponseDTO(
                        1L,
                        "Sextante",
                        EnumPaises.BRASIL
                );

        when(editoraRepository.existsByNome("Sextante"))
                .thenReturn(false);

        when(editoraMapper.toEntity(dto))
                .thenReturn(editora);

        when(editoraMapper.toResponseDTO(editora))
                .thenReturn(responseDTO);


        EditoraResponseDTO resultado =
                editoraService.createEditora(dto);

        assertNotNull(resultado);

        assertEquals("Sextante", resultado.nome());
        assertEquals(EnumPaises.BRASIL, resultado.paisOrigem());

        verify(editoraRepository).existsByNome("Sextante");
        verify(editoraRepository).save(editora);
    }

    @Test
    void naoDeveCadastrarEditoraComNomeJaCadastrado(){
        EditoraRequestDTO dto = new EditoraRequestDTO(
                "Sextante",
                EnumPaises.BRASIL
        );
        when(editoraRepository.existsByNome("Sextante")).thenReturn(true);

        try{
            editoraService.createEditora(dto);
        } catch(EditoraJaCadastrada ignored){}

        verify(editoraRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoEditoraJaCadastrada(){
        EditoraRequestDTO dto = new EditoraRequestDTO(
                "Sextante",
                EnumPaises.BRASIL
        );

        when(editoraRepository.existsByNome("Sextante")).thenReturn(true);

        assertThrows(
                EditoraJaCadastrada.class,
                () -> editoraService.createEditora(dto)
        );
    }

    //updateEditora
    @Test
    void deveAlterarEditoraComSucesso(){
        EditoraUpdateRequestDTO dto = new EditoraUpdateRequestDTO(
                "Sextante",
                EnumPaises.BRASIL
        );

        Long id = 1L;

        Editora editora = new Editora();
        editora.setNome("Sextante");
        editora.setPaisOrigem(EnumPaises.REINO_UNIDO);

        EditoraResponseDTO responseDTO = new EditoraResponseDTO(
                1L,
                "Sextante",
                EnumPaises.BRASIL
        );

        when(editoraRepository.findById(id))
                .thenReturn(Optional.of(editora));

        when(editoraRepository.existsByNome("Sextante"))
                .thenReturn(false);

        when(editoraMapper.toResponseDTO(editora))
                .thenReturn(responseDTO);

        EditoraResponseDTO resultado =
                editoraService.updateEditora(dto, id);

        assertNotNull(resultado);
        assertEquals(EnumPaises.BRASIL, editora.getPaisOrigem());
    }

    @Test
    void naoDeveAlterarEditoraComNomeJaCadastrada(){

        EditoraUpdateRequestDTO dto = new EditoraUpdateRequestDTO(
                "Sextante",
                null
        );

        Long id = 1L;

        Editora editora = new Editora();
        editora.setNome("Sextante");

        when(editoraRepository.findById(id)).thenReturn(Optional.of(editora));

        when(editoraRepository.existsByNome("Sextante"))
                .thenReturn(true);

        try {
            editoraService.updateEditora(dto, id);
        } catch (EditoraJaCadastrada ignored){}

        verify(editoraRepository, never())
                .save(any());
    }

}

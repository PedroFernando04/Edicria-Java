package com.qqd.edicria.controllers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Editora.EditoraUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.EditoraResponseDTO;
import com.qqd.edicria.mappers.tabelasPrincipais.EditoraMapper;
import com.qqd.edicria.services.tabelasPrincipais.EditoraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/publishers")
public class EditoraController {

    private final EditoraService editoraService;
    private final EditoraMapper editoraMapper;

    public EditoraController(
            EditoraService editoraService,
            EditoraMapper editoraMapper) {

        this.editoraService = editoraService;
        this.editoraMapper = editoraMapper;
    }

    @PostMapping
    public ResponseEntity<EditoraResponseDTO> createEditora(
            @Valid @RequestBody EditoraRequestDTO editoraRequestDTO){

        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(editoraService.createEditora(editoraRequestDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<EditoraResponseDTO> updateEditora(
            @RequestBody EditoraUpdateRequestDTO dto,
            @PathVariable Long id){

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(editoraService.updateEditora(dto, id));
    }

    @GetMapping("{id}")
    public ResponseEntity<EditoraResponseDTO> getEditora(
            @PathVariable Long id){

        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(editoraService.getEditora(id));
    }

    @GetMapping
    public ResponseEntity<List<EditoraResponseDTO>> getAllEditoras(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(editoraService.getAllEditoras());
    }
}

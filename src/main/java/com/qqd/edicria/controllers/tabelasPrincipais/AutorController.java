package com.qqd.edicria.controllers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Autor.AutorUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.AutorResponseDTO;
import com.qqd.edicria.mappers.tabelasPrincipais.AutorMapper;
import com.qqd.edicria.services.tabelasPrincipais.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/authors")
public class AutorController {

    private final AutorService autorService;
    private final AutorMapper autorMapper;

    public AutorController(AutorService autorService,  AutorMapper autorMapper) {
        this.autorService = autorService;
        this.autorMapper = autorMapper;
    }

    @PostMapping
    public ResponseEntity<AutorResponseDTO> createAutor (
            @Valid @RequestBody AutorRequestDTO autorRequestDTO){

        AutorResponseDTO responseDTO =
                autorService.createAutor(autorRequestDTO);

        return  ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorResponseDTO> updateAutor (
            @RequestBody AutorUpdateRequestDTO autorUpdateRequestDTO,
            @PathVariable Long id) {

        AutorResponseDTO resultado =
                autorService.updateAutor(autorUpdateRequestDTO, id);

        return  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorResponseDTO> getAutor (
            @PathVariable Long id) {

        AutorResponseDTO responseDTO =
                autorService.getAutor(id);

        return  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AutorResponseDTO>> getAllAutores (){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(autorService.getAll());
    }
}

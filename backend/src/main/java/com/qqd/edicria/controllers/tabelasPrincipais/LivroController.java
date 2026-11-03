package com.qqd.edicria.controllers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Livro.LivroUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.LivroResponseDTO;
import com.qqd.edicria.services.tabelasPrincipais.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> createLivro(
            @Valid @RequestBody LivroRequestDTO dto
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(livroService.createLivro(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> updateLivro(
            @RequestBody LivroUpdateRequestDTO dto,
            @PathVariable Long id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroService.updateLivro(dto, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> getLivro(
            @PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroService.getLivro(id));
    }

    @GetMapping("/titulo")
    public ResponseEntity<LivroResponseDTO> getLivroByTitulo(
            @RequestParam String titulo){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroService.getLivroByTitulo(titulo)
                );
    }

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> getAllLivros(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroService.getAllLivros());
    }
}

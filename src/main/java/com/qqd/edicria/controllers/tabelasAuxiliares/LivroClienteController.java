package com.qqd.edicria.controllers.tabelasAuxiliares;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LivroCliente.LivroClienteRequestDTO;
import com.qqd.edicria.dtos.response.tabelasAuxiliares.LivroClienteResponseDTO;
import com.qqd.edicria.mappers.tabelasAuxiliares.LivroClienteMapper;
import com.qqd.edicria.services.tabelasAuxiliares.LivroClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("book-user")
public class LivroClienteController {

    private final LivroClienteService livroClienteService;
    private final LivroClienteMapper livroClienteMapper;

    public LivroClienteController(
            LivroClienteService livroClienteService,
            LivroClienteMapper livroClienteMapper
    ) {
        this.livroClienteService = livroClienteService;
        this.livroClienteMapper = livroClienteMapper;
    }

    @PostMapping
    public ResponseEntity<LivroClienteResponseDTO> createLivroCliente(
            @Valid @RequestBody LivroClienteRequestDTO livroClienteRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(livroClienteService.createLivroCliente(livroClienteRequestDTO)
                );
    }

    @PutMapping("{idLivro}/{idUsuario}")
    public ResponseEntity<LivroClienteResponseDTO> updateLivroCliente(
            @Valid @RequestBody LivroClienteRequestDTO livroClienteRequestDTO,
            @PathVariable Long idLivro,
            @PathVariable Long idUsuario
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroClienteService
                        .updateLivroCliente(
                                livroClienteRequestDTO,
                                idLivro,
                                idUsuario
                        )
                );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLivroCliente(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(livroClienteService.deleteLivroCliente(id));
    }

    @GetMapping("{id}")
    public ResponseEntity<LivroClienteResponseDTO> getLivroClienteById(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(livroClienteService.getLivroCliente(id));
    }

    @GetMapping("{idLivro}/{idUsuario}")
    public ResponseEntity<LivroClienteResponseDTO> getLivroClienteByLivroAndUsuario(
            @PathVariable Long idLivro,
            @PathVariable Long idUsuario
    ){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(livroClienteService
                        .getLivroClienteByUserAndLivro(
                                idUsuario,
                                idLivro
                        )
                );
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<List<LivroClienteResponseDTO>> getLivroClienteByUserAndLivro(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(livroClienteService.getAllLivroClienteByUsuario(id));
    }
}

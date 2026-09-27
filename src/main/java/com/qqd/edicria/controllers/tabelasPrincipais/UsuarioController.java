package com.qqd.edicria.controllers.tabelasPrincipais;

import com.qqd.edicria.dtos.request.tabelasAuxiliares.LoginRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioRequestDTO;
import com.qqd.edicria.dtos.request.tabelasPrincipais.Usuario.UsuarioUpdateRequestDTO;
import com.qqd.edicria.dtos.response.tabelasPrincipais.UsuarioResponseDTO;
import com.qqd.edicria.services.tabelasPrincipais.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.createUsuario(usuarioRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getTodosUsuarios(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuario(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.getUsuario(id));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(
            @Valid @RequestBody UsuarioUpdateRequestDTO dto,
            Authentication authentication
    ) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(usuarioService.updateUsuario(dto, authentication.getName()));
    }


    @PostMapping("/login")
    public ResponseEntity<Void> autenticarUsuario(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        usuarioService.login(dto, request, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){

        usuarioService.logout(request, response);

        return ResponseEntity.ok().build();
    }
}
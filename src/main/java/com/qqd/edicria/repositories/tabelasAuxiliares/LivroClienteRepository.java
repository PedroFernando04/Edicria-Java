package com.qqd.edicria.repositories.tabelasAuxiliares;

import com.qqd.edicria.entities.tabelasAuxiliares.LivroCliente;
import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import com.qqd.edicria.entities.tabelasPrincipais.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LivroClienteRepository extends JpaRepository<LivroCliente, Long> {

    boolean existsByUsuarioAndLivro(Usuario usuario, Livro livro);

    List<LivroCliente> findAllByUsuario(Usuario usuario);

    Optional<LivroCliente> findByUsuarioAndLivro(Usuario usuario, Livro livro);

    Optional<LivroCliente> findByLivroIdAndUsuarioId(Long livroId, Long usuarioId);
}

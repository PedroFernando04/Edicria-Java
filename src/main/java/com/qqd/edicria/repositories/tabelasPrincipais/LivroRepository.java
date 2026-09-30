package com.qqd.edicria.repositories.tabelasPrincipais;

import com.qqd.edicria.entities.tabelasPrincipais.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{

    boolean existsByTitulo(String titulo);

    Optional<Livro> findByTitulo(String titulo);
}

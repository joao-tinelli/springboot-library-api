package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> , JpaSpecificationExecutor<Livro> {

    Optional<Livro> findByIsbn(String isbn);

    boolean existsByAutor(Autor autor);

}

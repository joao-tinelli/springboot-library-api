package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Injecao no container ja eh automatica
public interface AutorRepository extends JpaRepository<Autor, UUID> {
    List<Autor> findByNome(String nome);
    List<Autor> findByNacionalidade(String nacionalidade);
    List<Autor> findByNomeAndNacionalidade(String nome, String nacionalidade);
}

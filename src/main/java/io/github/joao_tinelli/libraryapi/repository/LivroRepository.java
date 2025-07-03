package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> {

    // select * from livro where id_autor = 'id' (Spring Data JPA vai fazer o mapeamento para mim)
    List<Livro> findByAutor(Autor autor);

    // obs: estou especificando para carregar o autor tambem (inicializao lazy)
    @Query("SELECT l FROM Livro l JOIN FETCH l.autor WHERE l.titulo = :titulo")
    List<Livro> findByTitulo(@Param("titulo") String titulo);

    List<Livro> findByIsbn(@Param("isbn") String isbn);

    // select * from livro where dataPublicacao between ? and ?
    List<Livro> findByDataPublicacaoBetween(LocalDate inicio, LocalDate fim);

    @Query("select l from Livro as l order by l.titulo")
    List<Livro> listarTodosOrdem();

    @Query("select a from Livro l join l.autor a")
    List<Autor> listarAutoresDosLivros();

    @Query("select l.titulo from Livro as l order by l.titulo")
    List<String> listarNomesLivros();

    @Query("select l.dataPublicacao from Livro as l")
    List<LocalDate> listarDataLivros();

    @Query("""
            select distinct l.genero
            from Livro l
            join l.autor a
            where a.nacionalidade = 'Brasil'
            order by l.genero
            """)
    List<String> listarOrdemGenerosAutoresBrasileiros();

    List<Livro> findByGenero(GeneroLivro generoLivro);

    // Positional Parameters
    @Query("select l from Livro l where l.genero = ?1 order by ?2")
    List<Livro> findByGeneroPositionalParameters(GeneroLivro generoLivro, String nomePropriedade);

    @Modifying
    @Query("delete from Livro where genero = ?1")
    void deleteByGenero(GeneroLivro generoLivro);

    @Modifying
    @Query("update Livro set dataPublicacao = ?1")
    void updateDataPublicacao(LocalDate novaData);

    boolean existsByAutor(Autor autor);

}

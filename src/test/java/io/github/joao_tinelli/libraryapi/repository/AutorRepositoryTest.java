package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AutorRepositoryTest {

    @Autowired
    AutorRepository repository;

    Autor autor;

    @BeforeEach
    void setUp() {
        autor = new Autor();
        autor.setNome("Joao");
        autor.setNacionalidade("Brasil");
        autor.setDataNascimento(LocalDate.parse("1990-01-01"));
    }

    @Test
    @DisplayName("Teste de salvar autor")
    void deveSalvarAutor(){
        // Arrange

        // Act
        Autor autorSalvo = repository.save(autor);

        // Assert
        assertNotNull(autorSalvo);
        assertNotNull(autorSalvo.getId());
        assertEquals("Joao", autorSalvo.getNome());
    }

    @Test
    @DisplayName("Teste de encontrar o autor por nome")
    void findByNome() {
        // Arrange
        repository.save(autor);

        // Act
        List<Autor> autores = repository.findByNome("Joao");

        // Assert
        assertFalse(autores.isEmpty());
        assertEquals("Joao", autores.get(0).getNome());
    }

    @Test
    @DisplayName("Teste de encontrar o autor por nacionalidade")
    void findByNacionalidade() {
        // Arrange
        repository.save(autor);

        // Act
        List<Autor> autores = repository.findByNacionalidade("Brasil");

        // Assert
        assertNotNull(autores);
        assertEquals("Brasil", autores.get(0).getNacionalidade());
    }

    @Test
    @DisplayName("Teste de encontrar o autor por nome e nacionalidade")
    void findByNomeAndNacionalidade() {
        // Arrange
        repository.save(autor);

        // Act
        List <Autor> autores = repository.findByNomeAndNacionalidade("Joao", "Brasil");

        // Assert
        assertNotNull(autores);
        assertEquals("Joao", autores.get(0).getNome());
        assertEquals("Brasil", autores.get(0).getNacionalidade());
    }

    @Test
    @DisplayName("Teste de encontrar o autor por nome, nacionalidade e data de nascimento")
    void findByNomeAndDataNascimentoAndNacionalidade() {
        // Arrange
        repository.save(autor);

        // Act
        Optional<Autor> optAutor = repository.findByNomeAndDataNascimentoAndNacionalidade("Joao", autor.getDataNascimento(), "Brasil");

        // Assert
        assertTrue(optAutor.isPresent());

        Autor autorEncontrado = optAutor.get();

        assertEquals("Joao", autorEncontrado.getNome());
        assertEquals("Brasil", autorEncontrado.getNacionalidade());
        assertEquals(LocalDate.parse("1990-01-01"), autorEncontrado.getDataNascimento());
    }

    @Test
    @DisplayName("Nao deve encontrar autor com dados inexistentes")
    void naoDeveEncontrarAutor(){
        // Arrange
        repository.save(autor);

        // Act
        Optional<Autor> resultado = repository.findByNomeAndDataNascimentoAndNacionalidade("Ana", LocalDate.of(1990, 01, 01), "Argentina");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve persistir autor no banco")
    void devePersistirAutor(){
        // Act
        Autor salvo = repository.save(autor);

        Optional<Autor> encontrado = repository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
    }
}
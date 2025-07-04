package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.repository.AutorRepository;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransacaoService {

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Transactional
    public void atualizacaoSemAtualizar(){
        var livro = livroRepository.findById(UUID.fromString("d05d3f25-6cdd-4c4a-81fc-5ab8ba337a99")).orElse(null);
        assert livro != null;
        livro.setDataPublicacao(LocalDate.of(2024, 12, 18));
        // livroRepository.save(livro) eh desnecessário, pois o commit dentro de uma transaction eh automatico
    }

    @Transactional
    public void executar(){
        // salva o autor
        Autor autor = new Autor();
        autor.setNome("Marcia");
        autor.setNacionalidade("Brasil");
        autor.setDataNascimento(LocalDate.of(1960,5,28));
        autorRepository.save(autor);

        // salva o livro
        Livro livro = new Livro();
        livro.setIsbn("987654321");
        livro.setPreco(BigDecimal.valueOf(10));
        livro.setGenero(GeneroLivro.ROMANCE);
        livro.setTitulo("Meu Primeiro Amor");
        livro.setDataPublicacao(LocalDate.of(1990, 3, 17));

        livro.setAutor(autor);
        livroRepository.save(livro);

        if (!autor.getNome().equals("Marcia")){
            throw new RuntimeException("Rollback!");
        }
    }
}

package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ToString
public class AutorRepositoryTest{

    @Autowired
    AutorRepository repository;

    @Autowired
    LivroRepository livroRepository;

    @Test
    public void salvarTest(){
        Autor autor = new Autor();
        autor.setNome("Pedro Henrique");
        autor.setNacionalidade("Brasil");
        autor.setDataNascimento(LocalDate.of(2008,8,24));

        var autorSalvo = repository.save(autor);
        System.out.println("Autor salvo: " + autorSalvo);
    }

    @Test
    public void atualizarTest(){
        var id = UUID.fromString("7ffbcaf5-f2cc-4e87-8ddc-e30b4de53e6b");
        Optional<Autor> possivelAutor = repository.findById(id);
        if(possivelAutor.isPresent()){

            Autor autorEncontrado = possivelAutor.get();
            System.out.println("Dados do autor: ");
            System.out.println(possivelAutor.get());

            autorEncontrado.setDataNascimento(LocalDate.of(1960, 1, 29));
            repository.save(autorEncontrado);

        }
    }

    @Test
    public void listarTest(){
        List<Autor> lista = repository.findAll();
        lista.forEach(System.out::println);
    }

    @Test
    public void countTest(){
        System.out.println("Contagem de autores: " + repository.count());
    }

    @Test
    public void deletePorIdTest(){
        var id = UUID.fromString("7ffbcaf5-f2cc-4e87-8ddc-e30b4de53e6b");
        repository.deleteById(id);
    }

    @Test
    public void deletePorObjetoTest(){
        var id = UUID.fromString("66d00236-806c-49e5-ba72-5e59c23e6411");
        var maria = repository.findById(id).get();
        repository.delete(maria);

    }

    @Test
    public void salvarAutorComLivrosTest(){
        Autor autor = new Autor();
        autor.setNome("Marcos");
        autor.setNacionalidade("Brasil");
        autor.setDataNascimento(LocalDate.of(1969,5,18));

        Livro livro = new Livro();
        livro.setIsbn("1234567");
        livro.setPreco(BigDecimal.valueOf(75));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("O Monge e o Executivo");
        livro.setDataPublicacao(LocalDate.of(2000, 1, 30));
        livro.setAutor(autor);

        autor.setLivros(new ArrayList<>());
        autor.getLivros().add(livro);

        repository.save(autor);
        // livroRepository.saveAll(autor.getLivros()); Com cascade, nao preciso salvar os livros manualmente
        // POREM, sempre que eu excluir um autor, todos os seus livros tbm serao excluidos!
    }

    @Test
    @Transactional // <-- ESSENCIAL para manter a sessão aberta e evitar LazyInitializationException
    public void listarLivrosAutorCorretamente(){
        var id = UUID.fromString("d9c0ee48-8f66-434e-8679-a14595aaf1ea");

        // 1. Busca o autor
        Autor autor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado para o ID: " + id));

        // 2. Apenas acesse a lista.
        // Dentro de uma transação, o Hibernate buscará os livros do banco de dados
        // automaticamente neste momento. Não é preciso chamar livroRepository.
        System.out.println("Listando livros para o autor: " + autor.getNome());
        autor.getLivros().forEach(System.out::println);
    }
}

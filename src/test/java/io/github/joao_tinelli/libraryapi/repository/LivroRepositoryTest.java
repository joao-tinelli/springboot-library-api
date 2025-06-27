package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Test
    void salvarTest(){
        Livro livro = new Livro();
        livro.setIsbn("98765");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("UFO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = autorRepository.findById(UUID.fromString("7a93d26f-bf41-4fc6-aaa8-ba33968b96b8")).orElse(null);

        livro.setAutor(autor);
        repository.save(livro);
    }

    @Test
    void salvarCascadeTest(){
        Livro livro = new Livro();
        livro.setIsbn("98765");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("UFO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = new Autor();
        autor.setNome("Joao");
        autor.setNacionalidade("Brasil");
        autor.setDataNascimento(LocalDate.of(1952,1,31));

        // Cascade ALL: Joao nao esta no banco, mas depois que eu salvar o livro ele sera salvo tbm!
        // Porem, se eu excluir esse livro, o autor tambem sera excluido!
        // ou seja, nao foi necessario chamar o metodo autoRepository.save(autor)

        livro.setAutor(autor);
        repository.save(livro);
    }

    @Test
    public void atualizarAutor(){
        UUID id = UUID.fromString("d05d3f25-6cdd-4c4a-81fc-5ab8ba337a99");
        var livroParaAtualizar = repository.findById(id).orElse(null); // Buscando o livro que vou alterar

        UUID idAutor = UUID.fromString("d526e8bc-e1da-4ab0-be32-cd9af3eb65a8");
        Autor autor = autorRepository.findById(idAutor).orElse(null); // Buscando o autor que vou colocar no livro

        livroParaAtualizar.setAutor(autor);
        repository.save(livroParaAtualizar); // Salvando/Atualizando o livro
    }

    @Test
    public void deletar(){
        UUID id = UUID.fromString("840cea44-05e1-4792-9735-5a6ea575bb75");
        repository.deleteById(id);
    }

    @Test
    @Transactional // para eu buscar o autor mesmo com inicializacao Lazy
    public void buscarLivroTeste(){
        UUID id = UUID.fromString("d05d3f25-6cdd-4c4a-81fc-5ab8ba337a99");
        Livro livro = repository.findById(id).orElse(null);
        System.out.println(livro.getTitulo());
        System.out.println(livro.getGenero());

        System.out.println(livro.getAutor().getNome());
    }


    @Test
    @Transactional
    void pesquisarPorIsbnTest(){
        List<Livro> lista = repository.findByIsbn("54321");
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void pesquisarPorData(){
        List<Livro> lista = repository.findByDataPublicacaoBetween(LocalDate.of(2020, 1, 1), LocalDate.of(2025, 1, 1));
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void pesquisarPorTituloOrdemTest(){
        List<Livro> lista = repository.listarTodosOrdem();
        lista.forEach(System.out::println);
    }


    @Test
    @Transactional
    void pesquisaPorTituloTest(){
        List<Livro> lista = repository.findByTitulo("Biografia de Antonio");
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void listarAutoresTest(){
        List<Autor> lista = repository.listarAutoresDosLivros();
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void listarNomesLivrosTest(){
        List<String> lista = repository.listarNomesLivros();
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void listarGenerosPorBrasileiros(){
        List<String> lista = repository.listarOrdemGenerosAutoresBrasileiros();
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void listarLivrosPorGenero(){
        List<Livro> lista = repository.findByGeneroPositionalParameters(GeneroLivro.FICCAO, "dataPublicacao");
        lista.forEach(System.out::println);
    }

    @Test
    @Transactional
    void deletarPorGenero() {
        // 1. Ação: Chama o método de deleção
        repository.deleteByGenero(GeneroLivro.CIENCIA);
        System.out.println("✅ Comando de deleção para o gênero 'CIENCIA' foi executado.");

        // 2. Verificação: Busca os livros do gênero que supostamente foi deletado
        List<Livro> livrosDeCiencia = repository.findByGenero(GeneroLivro.CIENCIA); // Supondo que este método exista

        System.out.println("🔎 Verificando no banco... Número de livros de 'CIENCIA' encontrados: " + livrosDeCiencia.size());

        // 3. Asserção: Confirma que a lista de livros para aquele gênero está vazia
        assertThat(livrosDeCiencia).isEmpty();

        // 4. Confirmação final no console
        if (livrosDeCiencia.isEmpty()) {
            System.out.println("🎉 Sucesso! A deleção foi confirmada dentro da transação.");
        }
    }

    @Test
    @Transactional
    @Commit
    void deletarPorGeneroPermanente() {
        repository.deleteByGenero(GeneroLivro.CIENCIA);
    }

    @Test
    @Transactional
    void updateDataPublicacaoTeste(){
        repository.updateDataPublicacao(LocalDate.of(2000, 1, 1));

        // Testando se as datas foram alteradas
        List<LocalDate> listaDatas = repository.listarDataLivros();
        listaDatas.forEach(System.out::println);
    }
}
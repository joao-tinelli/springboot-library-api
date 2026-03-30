package io.github.joao_tinelli.libraryapi.service;

import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.repository.AutorRepository;
import io.github.joao_tinelli.libraryapi.repository.LivroRepository;
import io.github.joao_tinelli.libraryapi.validator.AutorValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository repository;

    @Mock
    private AutorValidator validator;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private AutorService service;

    private Autor autor0;
    private Livro livro0;

    @BeforeEach
    void setUp() {
        autor0 = new Autor();
        autor0.setNome("Joao");
        autor0.setDataNascimento(LocalDate.of(2005, 02, 25));
        autor0.setNacionalidade("Brasil");

        Livro l1 = new Livro();
        l1.setAutor(autor0);
        l1.setTitulo("1984");

        autor0.setLivros(List.of(l1));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Testa salvar um autor que deve retornar um autor")
    void salvar() throws RegistroDuplicadoException {
        // Arrange
        // 1. Criamos um clone do seu autor0, mas COM UM ID preenchido,
        // para simular o que o banco de dados faria.
        Autor autorSalvo = new Autor();
        autorSalvo.setId(java.util.UUID.randomUUID()); // Use 1L se o seu ID for Long
        autorSalvo.setNome(autor0.getNome());
        autorSalvo.setDataNascimento(autor0.getDataNascimento());
        autorSalvo.setNacionalidade(autor0.getNacionalidade());
        autorSalvo.setLivros(autor0.getLivros());

        // 2. Configuramos o mock para retornar o autor COM ID
        when(repository.save(any(Autor.class))).thenReturn(autorSalvo);

        // Act
        // CORREÇÃO: Chamamos o método do Service!
        Autor resultado = service.salvar(autor0);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId()); // Agora isso vai passar com sucesso
        assertEquals(autor0.getNome(), resultado.getNome());

        // Garante que o service chamou as dependências corretamente
        verify(validator, times(1)).validar(autor0);
        verify(repository, times(1)).save(autor0);
    }

    @Test
    @DisplayName("Testa salvar um autor que deve retornar RegistroDuplicadoException")
    void salvarAutorDuplicado() throws RegistroDuplicadoException {
        // Arrange
        // Como o método validar() retorna void, usamos a sintaxe doThrow().when().metodo()
        doThrow(RegistroDuplicadoException.class)
                .when(validator)
                .validar(any(Autor.class));

        // Act & Assert
        // Executamos o service e verificamos se ele repassa a exceção lançada pelo validator
        Assertions.assertThrows(
                RegistroDuplicadoException.class,
                () -> service.salvar(autor0),
                "Deveria lançar exceção de Autor já cadastrado"
        );

        // Garante que o service chamou a validação
        verify(validator, times(1)).validar(autor0);

        // Garante que o service NÃO tentou salvar no banco de dados após o erro
        verify(repository, never()).save(any(Autor.class));
    }

    @Test
    @DisplayName("Testa atualizar um autor com sucesso")
    void atualizar() throws RegistroDuplicadoException {
        // Arrange
        autor0.setId(UUID.randomUUID());
        when(repository.save(any(Autor.class))).thenReturn(autor0);

        // Act
        assertDoesNotThrow(() -> service.atualizar(autor0));

        // Assert
        verify(validator, times(1)).validar(autor0);
        verify(repository, times(1)).save(autor0);
    }

    @Test
    @DisplayName("Testa atualizar um autor que nao existe e lanca IllegalArgumentException")
    void atualizarLancaExcecao() throws IllegalArgumentException, RegistroDuplicadoException {
        // Arrange
        Autor autorInexistente = new Autor();

        // Act + Assert
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(autorInexistente),
                "Autor nao encontrado"
        );

        // Assert
        verify(validator, times(0)).validar(autorInexistente);
        verify(repository, times(0)).save(autorInexistente);
        assertEquals("Autor nao encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Testa encontrar autor por Id com sucesso")
    void obterPorId() {
        // Arrange
        autor0.setId(UUID.randomUUID());
        when(repository.findById(autor0.getId())).thenReturn(Optional.of(autor0));

        // Act
        Optional<Autor> autorEncontrado = service.obterPorId(autor0.getId());

        // Assert
        assertTrue(autorEncontrado.isPresent());
        assertEquals(autor0, autorEncontrado.get());
        verify(repository, times(1)).findById(autor0.getId());
    }

    @Test
    @DisplayName("Testa obterPorId quando autor não é encontrado")
    void obterPorIdNaoEncontrado(){
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        Optional <Autor> autorEncontrado = service.obterPorId(idInexistente);

        // Assert
        assertFalse(autorEncontrado.isPresent());
        verify(repository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Testa deletar um autor com sucesso")
    void deletar() {
        // Arrange
        autor0.setId(UUID.randomUUID());

        // Explicitamos que, neste cenário, o autor NÃO possui livros cadastrados
        when(livroRepository.existsByAutor(autor0)).thenReturn(false);

        // Não precisamos de "when" para o repository.delete() porque ele é void.

        // Act
        // Lança a ação usando assertDoesNotThrow apenas para deixar claro
        // visualmente que não esperamos nenhuma exceção aqui
        assertDoesNotThrow(() -> service.deletar(autor0));

        // Assert
        // Verifica se a checagem de livros foi feita
        verify(livroRepository, times(1)).existsByAutor(autor0);

        // Verifica se o delete foi realmente chamado
        verify(repository, times(1)).delete(autor0);
    }

    @Test
    @DisplayName("Teste deletar um autor que possui livros cadastrados e deve lancar OperacaoNaoPermitidaException")
    void deletarAutorComLivrosCadastrados() throws OperacaoNaoPermitidaException {
        // Arrange
        autor0.setId(UUID.randomUUID());
        when(livroRepository.existsByAutor(autor0)).thenReturn(true);

        // Act / Assert
        OperacaoNaoPermitidaException oe = Assertions.assertThrows(
                OperacaoNaoPermitidaException.class,
                () -> service.deletar(autor0),
                "Não é permitido excluir um autor que possui livros cadastrados!"
        );

        // Assert
        verify(repository, times(0)).delete(autor0);
        assertEquals("Não é permitido excluir um autor que possui livros cadastrados!", oe.getMessage());
    }

    @Test
    @DisplayName("Testa pesquisa com nome e nacionalidade")
    void pesquisaComNomeENacionalidade() {
        // Arrange
        when(repository.findByNomeAndNacionalidade("Machado", "Brasileiro"))
                .thenReturn(List.of(autor0));

        // Act
        List<Autor> resultado = service.pesquisa("Machado", "Brasileiro");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByNomeAndNacionalidade("Machado", "Brasileiro");
        verify(repository, never()).findByNome(any());
        verify(repository, never()).findByNacionalidade(any());
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Testa pesquisa apenas com nome")
    void pesquisaApenasComNome() {
        // Arrange
        when(repository.findByNome("Machado")).thenReturn(List.of(autor0));

        // Act
        List<Autor> resultado = service.pesquisa("Machado", null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByNome("Machado");
        verify(repository, never()).findByNomeAndNacionalidade(any(), any());
        verify(repository, never()).findByNacionalidade(any());
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Testa pesquisa apenas com nacionalidade")
    void pesquisaApenasComNacionalidade() {
        // Arrange
        when(repository.findByNacionalidade("Brasileiro")).thenReturn(List.of(autor0));

        // Act
        List<Autor> resultado = service.pesquisa(null, "Brasileiro");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByNacionalidade("Brasileiro");
        verify(repository, never()).findByNomeAndNacionalidade(any(), any());
        verify(repository, never()).findByNome(any());
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Testa pesquisa sem filtros retorna todos")
    void pesquisaSemFiltros() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(autor0));

        // Act
        List<Autor> resultado = service.pesquisa(null, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByNomeAndNacionalidade(any(), any());
        verify(repository, never()).findByNome(any());
        verify(repository, never()).findByNacionalidade(any());
    }

    @Test
    @DisplayName("Testa pesquisaByExample com nome e nacionalidade")
    void pesquisaByExample() {
        // Arrange
        Page<Autor> paginaMock = new PageImpl<>(List.of(autor0));
        when(repository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(paginaMock);

        // Act
        Page<Autor> resultado = service.pesquisaByExample("Machado", "Brasileiro", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(repository, times(1)).findAll(any(Example.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Testa pesquisaByExample sem filtros retorna página vazia")
    void pesquisaByExampleSemResultados() {
        // Arrange
        Page<Autor> paginaVazia = Page.empty();
        when(repository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(paginaVazia);

        // Act
        Page<Autor> resultado = service.pesquisaByExample(null, null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalElements());
        verify(repository, times(1)).findAll(any(Example.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Testa possuiLivro quando autor tem livros")
    void possuiLivroRetornaTrue() {
        // Arrange
        when(livroRepository.existsByAutor(autor0)).thenReturn(true);

        // Act
        boolean resultado = service.possuiLivro(autor0);

        // Assert
        assertTrue(resultado);
        verify(livroRepository, times(1)).existsByAutor(autor0);
    }

    @Test
    @DisplayName("Testa possuiLivro quando autor não tem livros")
    void possuiLivroRetornaFalse() {
        // Arrange
        when(livroRepository.existsByAutor(autor0)).thenReturn(false);

        // Act
        boolean resultado = service.possuiLivro(autor0);

        // Assert
        assertFalse(resultado);
        verify(livroRepository, times(1)).existsByAutor(autor0);
    }
}
package io.github.joao_tinelli.libraryapi.controller;

import io.github.joao_tinelli.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.joao_tinelli.libraryapi.controller.dto.ErroResposta;
import io.github.joao_tinelli.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.joao_tinelli.libraryapi.controller.mappers.LivroMapper;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.model.Usuario;
import io.github.joao_tinelli.libraryapi.service.LivroService;
import io.github.joao_tinelli.libraryapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
@Tag(name = "Books")
public class LivroController implements GenericController{
    private final LivroService service;
    private final LivroMapper mapper;
    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Save", description = "Register a new book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registered with success."),
            @ApiResponse(responseCode = "422", description = "Validation error."),
            @ApiResponse(responseCode = "409", description = "Book already registered.")
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto, Authentication authentication) throws RegistroDuplicadoException {
        // mapear DTO para entidade
        Livro livro = mapper.toEntity(dto);

        UserDetails usuarioLogado = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.obterPorLogin(usuarioLogado.getUsername());
        livro.setIdUsuario(usuario.getId());

        // enviar a entidade para o service validar e salvar na base
        service.salvar(livro);

        // criar url para acesso dos dados do livro
        var url = gerarHeaderLocation(livro.getId());

        // retornar codigo created com header location
        return ResponseEntity.created(url).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Get details", description = "Get a book's details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found."),
            @ApiResponse(responseCode = "404", description = "Book not found.")
    })
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(@PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = mapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet( () -> ResponseEntity.notFound().build() );
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Delete", description = "Delete a book.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Book not found.")
    })
    public ResponseEntity<ErroResposta> excluir(@PathVariable("id") String id){
        var idLivro = UUID.fromString(id);
        Optional<Livro> livroOptional = service.obterPorId(idLivro);

        if (livroOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        service.deletar(livroOptional.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Search books", description = "Search for books using filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book(s) found.")
    })
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisa(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "nome-autor", required = false) String nomeAutor,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "ano-publicacao", required = false) Integer anoPublicacao,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina
    ){
        Page<Livro> paginaResultado = service.pesquisa(isbn, titulo, nomeAutor, genero, anoPublicacao, pagina, tamanhoPagina);

        Page<ResultadoPesquisaLivroDTO> resultado = paginaResultado.map(mapper::toDTO);

        return ResponseEntity.ok(resultado);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Update", description = "Update a book.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Updated with success."),
            @ApiResponse(responseCode = "422", description = "Validation error."),
            @ApiResponse(responseCode = "409", description = "Book already registered.")
    })
    public ResponseEntity<?> atualizar(@PathVariable("id") String id, @RequestBody @Valid CadastroLivroDTO dto){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    Livro entidadeAux = mapper.toEntity(dto);

                    livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                    livro.setIsbn(entidadeAux.getIsbn());
                    livro.setPreco(entidadeAux.getPreco());
                    livro.setGenero(entidadeAux.getGenero());
                    livro.setAutor(entidadeAux.getAutor());
                    livro.setTitulo(entidadeAux.getTitulo());

                    try {
                        service.atualizar(livro);
                    } catch (RegistroDuplicadoException e) {
                        throw new RuntimeException(e);
                    }

                    // Isso retorna ResponseEntity<Void>, que é compatível com ResponseEntity<?>
                    return ResponseEntity.noContent().build();

                    // O .build() aqui retorna ResponseEntity<Object>, que agora é compatível com a assinatura ResponseEntity<?>
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

package io.github.joao_tinelli.libraryapi.controller;

import io.github.joao_tinelli.libraryapi.controller.dto.AutorDTO;
import io.github.joao_tinelli.libraryapi.controller.dto.ErroResposta;
import io.github.joao_tinelli.libraryapi.controller.mappers.AutorMapper;
import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autores") // http://localhost:8080/autores
@RequiredArgsConstructor
public class AutorController {

    // Injecao de dependencia
    private final AutorService service;
    private final AutorMapper mapper;

    @PostMapping // Metodo: POST
    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDTO dto){ // @RequestBody: essa annotation indica que esse objeto (dto) vai vir no body

        try {
            Autor autorEntidade = mapper.toEntity(dto); // <---
            service.salvar(autorEntidade); // Salvando no banco de dados

            // Pega a URI da request atual para retornar uma URI do tipo: http://localhost:8080/autores/id
            // Conforme exigido pelo contrato da API
            URI location = ServletUriComponentsBuilder.
                    fromCurrentRequest().
                    path("/{id}").
                    buildAndExpand(autorEntidade.getId()).
                    toUri();

            // ResponseEntity: classe que representa um objeto response
            return ResponseEntity.created(location).build();

        } catch (RegistroDuplicadoException rde){
            var erroDTO = ErroResposta.conflito(rde.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<AutorDTO> obterDetalhes(@PathVariable("id") String id){
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);

        if (autorOptional.isPresent()){
            Autor autor = autorOptional.get();
            AutorDTO dto = mapper.toDTO(autor); // <----

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ErroResposta> excluir(@PathVariable("id") String id) throws OperacaoNaoPermitidaException {
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if (autorOptional.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            service.deletar(autorOptional.get());

            return ResponseEntity.noContent().build();

        } catch (OperacaoNaoPermitidaException e){
            var erroResposta = ErroResposta.respostaPadrao(e.getMessage());
            return ResponseEntity.status(erroResposta.status()).body(erroResposta);
        }
    }

    @GetMapping
    public ResponseEntity<List<AutorDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade){

        List<Autor> resultado = service.pesquisaByExample(nome, nacionalidade);
        List<AutorDTO> lista = resultado.
                stream().
                map(autor -> new AutorDTO
                        (autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(),
                        autor.getNacionalidade())
                ).collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(@PathVariable("id") String id, @RequestBody @Valid AutorDTO dto) throws RegistroDuplicadoException {

        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var autor = autorOptional.get();
            autor.setNome(dto.nome());
            autor.setNacionalidade(dto.nacionalidade());
            autor.setDataNascimento(dto.dataNascimento());

            service.atualizar(autor);
            return ResponseEntity.noContent().build();

        } catch (RegistroDuplicadoException rde){
            var erroDTO = ErroResposta.conflito(rde.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}

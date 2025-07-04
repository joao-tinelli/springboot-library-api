package io.github.joao_tinelli.libraryapi.controller;

import io.github.joao_tinelli.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.joao_tinelli.libraryapi.controller.dto.ErroResposta;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController {
    private final LivroService livroService;

    /*
    *
    *     @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody @Valid CadastroLivroDTO dto){
        try {
            // mapear DTO para entidade
            // enviar a entidade para o service validar e salvar na base
            // criar url para acesso dos dados do livro
            // retornar codigo created com header location
            return null;

        } catch (RegistroDuplicadoException e){
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
    * */


}

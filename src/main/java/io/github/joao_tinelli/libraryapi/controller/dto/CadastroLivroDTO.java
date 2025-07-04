package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.GeneroLivro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastroLivroDTO(
       UUID id,
       String isbn,
       String titulo,
       LocalDate dataPublicacao,
       GeneroLivro generoLivro,
       BigDecimal preco,
       UUID idAutor,
       AutorDTO autor
) {
}

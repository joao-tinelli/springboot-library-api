package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Book Response Result")
public record ResultadoPesquisaLivroDTO(
        @Schema(name = "id")
        UUID id,
        @Schema(name = "isbn")
        String isbn,
        @Schema(name = "titulo")
        String titulo,
        @Schema(name = "dataPublicacao")
        LocalDate dataPublicacao,
        @Schema(name = "genero")
        GeneroLivro genero,
        @Schema(name = "preco")
        BigDecimal preco,
        @Schema(name = "autor")
        AutorDTO autor
) {
}

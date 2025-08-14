package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Book")
public record CadastroLivroDTO(
       @ISBN
       @NotBlank(message = "campo obrigatório")
       @Schema(name = "isbn")
       String isbn,

       @NotBlank(message = "campo obrigatório")
       @Schema(name = "titulo")
       String titulo,

       @NotNull(message = "campo obrigatório")
       @Past(message = "deve ser uma data passada")
       @Schema(name = "dataPublicacao")
       LocalDate dataPublicacao,

       @Schema(name = "genero")
       GeneroLivro genero,

       @Schema(name = "preco")
       BigDecimal preco,

       @NotNull(message = "campo obrigatório")
       @Schema(name = "idAutor")
       UUID idAutor
) {
}

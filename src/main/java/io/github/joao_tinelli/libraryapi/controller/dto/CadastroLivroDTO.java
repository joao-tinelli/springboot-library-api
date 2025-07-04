package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.GeneroLivro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastroLivroDTO(
       @ISBN
       @NotBlank(message = "campo obrigatório")
       String isbn,
       @NotBlank(message = "campo obrigatório")
       String titulo,
       @NotNull(message = "campo obrigatório")
       @Past(message = "deve ser uma data passada")
       LocalDate dataPublicacao,
       GeneroLivro generoLivro,
       BigDecimal preco,
       @NotNull(message = "campo obrigatório")
       UUID idAutor
) {
}

package io.github.joao_tinelli.libraryapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

// DTO: Data Transfer Object
// Contrato da API define que para criar um autor, o cliente deve informar apenas o nome, data de nascimento e nacionalidade
// Mas como a entidade Autor tem mais atributos, deve-se criar o record AutorDTO
// AutorDTO é uma camada representacional, ou seja, só serve pra representar um json
@Schema(name = "Author")
public record AutorDTO(
        @Schema(name = "id")
        UUID id,

        @NotBlank(message = "campo obrigatório")
        @Size(min=2, max = 100, message = "campo fora do tamanho permitido")
        @Schema(name = "nome")
        String nome,

        @NotNull(message = "campo obrigatório")
        @Past(message = "não pode ser uma data futura")
        @Schema(name = "dataNascimento")
        LocalDate dataNascimento,

        @Size(min=2, max = 50, message = "campo fora do tamanho permitido")
        @NotBlank(message = "campo obrigatório")
        @Schema(name = "nacionalidade")
        String nacionalidade
)
{
}



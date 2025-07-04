package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.Autor;
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
public record AutorDTO(
        UUID id,
        @NotBlank(message = "campo obrigatório")
        @Size(min=2, max = 100, message = "campo fora do tamanho permitido")
        String nome,
        @NotNull(message = "campo obrigatório")
        @Past(message = "não pode ser uma data futura")
        LocalDate dataNascimento,
        @Size(min=2, max = 50, message = "campo fora do tamanho permitido")
        @NotBlank(message = "campo obrigatório")
        String nacionalidade
)
{

    public Autor mapearParaAutor(){
        Autor autor = new Autor();
        autor.setNome(this.nome);
        autor.setDataNascimento(this.dataNascimento);
        autor.setNacionalidade(this.nacionalidade);
        return autor;
    }
}



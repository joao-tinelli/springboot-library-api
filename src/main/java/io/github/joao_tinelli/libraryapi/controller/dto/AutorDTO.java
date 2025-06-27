package io.github.joao_tinelli.libraryapi.controller.dto;

import io.github.joao_tinelli.libraryapi.model.Autor;

import java.time.LocalDate;
import java.util.UUID;

// DTO: Data Transfer Object
// Contrato da API define que para criar um autor, o cliente deve informar apenas o nome, data de nascimento e nacionalidade
// Mas como a entidade Autor tem mais atributos, deve-se criar o record AutorDTO
// AutorDTO é uma camada representacional, ou seja, só serve pra representar um json
public record AutorDTO(
        UUID id, String nome, LocalDate dataNascimento, String nacionalidade) {

    public Autor mapearParaAutor(){
        Autor autor = new Autor();
        autor.setNome(this.nome);
        autor.setDataNascimento(this.dataNascimento);
        autor.setNacionalidade(this.nacionalidade);
        return autor;
    }
}



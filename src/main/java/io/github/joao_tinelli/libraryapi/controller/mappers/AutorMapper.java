package io.github.joao_tinelli.libraryapi.controller.mappers;

import io.github.joao_tinelli.libraryapi.controller.dto.AutorDTO;
import io.github.joao_tinelli.libraryapi.model.Autor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    // Conversao eh feita em tempo de execucao
    Autor toEntity(AutorDTO dto);
    AutorDTO toDTO(Autor autor);
}

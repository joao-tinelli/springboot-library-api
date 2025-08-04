package io.github.joao_tinelli.libraryapi.controller.mappers;

import io.github.joao_tinelli.libraryapi.controller.dto.UsuarioDTO;
import io.github.joao_tinelli.libraryapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioDTO dto);
}

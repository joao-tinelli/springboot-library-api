package io.github.joao_tinelli.libraryapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Error field")
public record ErroCampo(String campo, String erro) {

}


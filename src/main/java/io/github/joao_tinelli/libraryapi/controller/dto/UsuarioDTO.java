package io.github.joao_tinelli.libraryapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
@Schema(name = "User")
public record UsuarioDTO(
        @NotBlank(message = "campo obrigatório")
        @Schema(name = "login")
        String login,
        @NotBlank(message = "campo obrigatório")
        @Schema(name = "senha")
        String senha,
        @Email(message = "email invalido")
        @Schema(name = "email")
        String email,
        @Schema(name = "roles")
        List<String> roles) {
}

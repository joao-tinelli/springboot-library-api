package io.github.joao_tinelli.libraryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.joao_tinelli.libraryapi.config.SecurityConfiguration;
import io.github.joao_tinelli.libraryapi.config.TestSecurityConfig;
import io.github.joao_tinelli.libraryapi.config.WebConfiguration;
import io.github.joao_tinelli.libraryapi.controller.dto.AutorDTO;
import io.github.joao_tinelli.libraryapi.controller.mappers.AutorMapper;
import io.github.joao_tinelli.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.joao_tinelli.libraryapi.exception.RegistroDuplicadoException;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.model.Usuario;

import io.github.joao_tinelli.libraryapi.service.AutorService;
import io.github.joao_tinelli.libraryapi.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AutorController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                OAuth2AuthorizationServerAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                ),
                @ComponentScan.Filter(          // ← adiciona este
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebConfiguration.class
                )
        }
)
@Import(TestSecurityConfig.class)
class AutorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutorService service;

    @MockitoBean
    private AutorMapper mapper;

    @MockitoBean
    private UsuarioService usuarioService;


    private AutorDTO dto;
    private Autor autorMapeado;
    private Usuario usuarioLogado;
    UUID id;

    @BeforeEach
    void setUp() {
        dto = new AutorDTO(
                null,
                "Machado de Assis",
                LocalDate.of(1839, 6, 21),
                "Brasileiro"
        );

        autorMapeado = new Autor();
        autorMapeado.setNome(dto.nome());
        autorMapeado.setDataNascimento(dto.dataNascimento());
        autorMapeado.setNacionalidade(dto.nacionalidade());

        Livro l1 = new Livro();
        l1.setAutor(autorMapeado);
        l1.setTitulo("1984");

        autorMapeado.setLivros(List.of(l1));

        id = UUID.randomUUID();

        usuarioLogado = new Usuario();
        usuarioLogado.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Deve salvar um autor e retornar 201 com Location header")
    @WithMockUser(roles = "GERENTE")
    void salvar() throws Exception {
        // Arrange
        Autor autorSalvo = new Autor();
        autorSalvo.setId(id);
        autorSalvo.setNome(dto.nome());

        when(mapper.toEntity(any(AutorDTO.class))).thenReturn(autorMapeado);
        when(usuarioService.obterPorLogin(any())).thenReturn(usuarioLogado);
        when(service.salvar(any(Autor.class))).thenReturn(autorSalvo);

        // Act & Assert
        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando o autor já estiver cadastrado")
    @WithMockUser(roles = "GERENTE")
    void salvarAutorDuplicado() throws Exception {
        // Arrange
        autorMapeado.setId(id);
        when(mapper.toEntity(any())).thenReturn(autorMapeado);
        when(usuarioService.obterPorLogin(any())).thenReturn(usuarioLogado);
        doThrow(RegistroDuplicadoException.class).when(service).salvar(any(Autor.class));

        // Act & Assert
        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário não tem role GERENTE")
    @WithMockUser(roles = "OPERADOR")
    void salvarSemPermissao() throws Exception {
        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @DisplayName("Deve retornar 422 quando DTO é inválido")
    @WithMockUser(roles = "GERENTE")
    void salvarDtoInvalido() throws Exception {
        AutorDTO dtoInvalido = new AutorDTO(null, null, null, null);
        when(usuarioService.obterPorLogin(any())).thenReturn(usuarioLogado);

        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isUnprocessableEntity()); // 422
    }

    @Test
    @DisplayName("Deve retornar 200 quando o autor é encontrado")
    @WithMockUser(roles = "OPERADOR")
    void buscarAutorValido() throws Exception {
        // Arrange
        autorMapeado.setId(id);

        when(service.obterPorId(id)).thenReturn(Optional.of(autorMapeado));

        AutorDTO dtoEsperado = new AutorDTO(
                id,
                autorMapeado.getNome(),
                autorMapeado.getDataNascimento(),
                autorMapeado.getNacionalidade()
        );
        when(mapper.toDTO(autorMapeado)).thenReturn(dtoEsperado);

        // Act & Assert
        mockMvc.perform(get("/autores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(autorMapeado.getNome()))
                .andExpect(jsonPath("$.nacionalidade").value(autorMapeado.getNacionalidade()));

        verify(service, times(1)).obterPorId(id);
        verify(mapper, times(1)).toDTO(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 404 quando o autor não é encontrado")
    @WithMockUser(roles = "OPERADOR")
    void buscarAutorInvalido() throws Exception {
        // Arrange
        when(service.obterPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/autores/" + id))
                .andExpect(status().isNotFound()); // 404

        verify(service, times(1)).obterPorId(id);
        verify(mapper, times(0)).toDTO(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 204 quando o autor é excluído com sucesso")
    @WithMockUser(roles = "GERENTE")
    void deletarAutorValido() throws Exception{
        // Arrange
        autorMapeado.setId(id);
        when(service.obterPorId(id)).thenReturn(Optional.of(autorMapeado));

        // Act & Assert
        mockMvc.perform(delete("/autores/" + id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(1)).deletar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 404 quando o autor não for encontrado")
    @WithMockUser(roles = "GERENTE")
    void deletarAutorInexistente() throws Exception{
        // Arrange
        when(service.obterPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/autores/" + id))
                .andExpect(status().isNotFound());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(0)).deletar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 403 quando o usuário não tem autorização")
    @WithMockUser(roles = "OPERADOR")
    void deletarSemPermissao() throws Exception {
        mockMvc.perform(delete("/autores/" + id))
                .andExpect(status().isForbidden());

        verify(service, times(0)).obterPorId(id);
        verify(service, times(0)).deletar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 400 quando o autor possui livros cadastrados")
    @WithMockUser(roles = "GERENTE")
    void deletarAutorComLivros() throws Exception {
        // Arrange
        autorMapeado.setId(id);
        when(service.obterPorId(id)).thenReturn(Optional.of(autorMapeado));
        doThrow(OperacaoNaoPermitidaException.class).when(service).deletar(autorMapeado);

        // Act & Assert
        mockMvc.perform(delete("/autores/" + id))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(1)).deletar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 200 ao pesquisar autores")
    @WithMockUser(roles = "OPERADOR")
    void pesquisarAutores() throws Exception {
        // Arrange
        String nome = "Machado";
        String nacionalidade = "Brasileiro";
        int pagina = 0;
        int tamanhoPagina = 10;

        org.springframework.data.domain.Page<Autor> paginaAutores = new org.springframework.data.domain.PageImpl<>(List.of(autorMapeado));

        when(service.pesquisaByExample(nome, nacionalidade, pagina, tamanhoPagina)).thenReturn(paginaAutores);
        AutorDTO dtoEsperado = new AutorDTO(id, autorMapeado.getNome(), autorMapeado.getDataNascimento(), autorMapeado.getNacionalidade());
        when(mapper.toDTO(autorMapeado)).thenReturn(dtoEsperado);

        // Act & Assert
        mockMvc.perform(get("/autores")
                        .param("nome", nome)
                        .param("nacionalidade", nacionalidade)
                        .param("pagina", String.valueOf(pagina))
                        .param("tamanho-pagina", String.valueOf(tamanhoPagina)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].nome").value(autorMapeado.getNome()))
                .andExpect(jsonPath("$.content[0].nacionalidade").value(autorMapeado.getNacionalidade()));

        verify(service, times(1)).pesquisaByExample(nome, nacionalidade, pagina, tamanhoPagina);
        verify(mapper, times(1)).toDTO(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 204 ao atualizar um autor com sucesso")
    @WithMockUser(roles = "GERENTE")
    void atualizarAutorValido() throws Exception {
        // Arrange
        autorMapeado.setId(id);
        when(service.obterPorId(id)).thenReturn(Optional.of(autorMapeado));
        doNothing().when(service).atualizar(autorMapeado);

        // Act & Assert
        mockMvc.perform(put("/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(1)).atualizar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar autor inexistente")
    @WithMockUser(roles = "GERENTE")
    void atualizarAutorInexistente() throws Exception {
        // Arrange
        when(service.obterPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(0)).atualizar(any());
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar atualizar com dados já existentes")
    @WithMockUser(roles = "GERENTE")
    void atualizarAutorComConflito() throws Exception {
        // Arrange
        autorMapeado.setId(id);
        when(service.obterPorId(id)).thenReturn(Optional.of(autorMapeado));
        doThrow(RegistroDuplicadoException.class).when(service).atualizar(autorMapeado);

        // Act & Assert
        mockMvc.perform(put("/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        verify(service, times(1)).obterPorId(id);
        verify(service, times(1)).atualizar(autorMapeado);
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar atualizar sem permissão")
    @WithMockUser(roles = "OPERADOR")
    void atualizarAutorSemPermissao() throws Exception {
        mockMvc.perform(put("/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(service, times(0)).obterPorId(id);
    }

    @Test
    @DisplayName("Deve retornar 422 ao tentar atualizar com payload inválido")
    @WithMockUser(roles = "GERENTE")
    void atualizarAutorDtoInvalido() throws Exception {
        AutorDTO dtoInvalido = new AutorDTO(null, null, null, null);

        mockMvc.perform(put("/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isUnprocessableEntity());

        verify(service, times(0)).obterPorId(id);
    }

}
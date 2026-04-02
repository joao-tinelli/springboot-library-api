package io.github.joao_tinelli.libraryapi.integrationtests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.joao_tinelli.libraryapi.config.TestConfigs;
import io.github.joao_tinelli.libraryapi.controller.dto.AutorDTO;
import io.github.joao_tinelli.libraryapi.integration.AbstractIntegrationTest;
import io.github.joao_tinelli.libraryapi.model.Autor;
import io.github.joao_tinelli.libraryapi.model.Livro;
import io.github.joao_tinelli.libraryapi.model.Usuario;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.mockito.Mockito;
import io.github.joao_tinelli.libraryapi.service.UsuarioService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AutorControllerIntegrationTest extends AbstractIntegrationTest {

    // Definir como vai ser a request
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static AutorDTO dto;
    private static Autor autorMapeado;
    private static Usuario usuarioLogado;
    private static UUID id;
    private static UUID idAutorCriado;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @MockitoBean
    UsuarioService usuarioService;

    @BeforeAll
    public static void setup(){
        // Arrange
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .build();

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
        usuarioLogado.setRoles(List.of("GERENTE"));
        usuarioLogado.setLogin("usuario-teste");
    }

    @BeforeEach
    void setupMockJwt() {
        Jwt jwt = Jwt.withTokenValue("token-falso")
                .header("alg", "none")
                .claim("sub", "usuario-teste")
                .claim("scope", "GERENTE") // Como o Security usa setAuthorityPrefix("ROLE_"), isso vira ROLE_GERENTE
                .build();
        
        Mockito.when(jwtDecoder.decode("token-falso")).thenReturn(jwt);
        Mockito.when(usuarioService.obterPorLogin("usuario-teste")).thenReturn(usuarioLogado);
    }

    @Test
    @Order(1)
    @DisplayName("Salvar um autor deve retornar status 201")
    void deveSalvarAutorComSucesso() throws JsonProcessingException {
        // Arrange: Preparamos o corpo da requisição convertendo o DTO para string JSON
        String payload = objectMapper.writeValueAsString(dto);

        // Act & Assert: Executamos a requisição e validamos a resposta numa mesma cadeia fluente
        String location = RestAssured.given()
                .spec(specification) // Utiliza as configurações definidas no setup (porta, log, etc)
                .contentType(ContentType.JSON) // Informa que o envio é um JSON
                .header("Authorization", "Bearer token-falso") // Enviamos nosso token fake
                .body(payload) // Corpo da requisição com os dados do autor
            .when() // Ação (Quando)
                .post("/autores") // Requisição POST na rota de autores
            .then() // Asserção (Então)
                .statusCode(201) // Valida que a resposta HTTP é 201 Created
                .header("Location", Matchers.notNullValue())
                .extract().header("Location"); // Extrai o cabeçalho Location para capturar o ID

        idAutorCriado = UUID.fromString(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    @Order(2)
    @DisplayName("Salvar autor com usuário sem permissão (OPERADOR) deve retornar 403")
    void deveRetornar403QuandoUsuarioNaoForGerente() throws JsonProcessingException {
        // Arrange
        String payload = objectMapper.writeValueAsString(dto); // Mesmo Machado de Assis

        Jwt jwtOperador = Jwt.withTokenValue("token-operador")
                .header("alg", "none")
                .claim("sub", "usuario-operador")
                .claim("scope", "OPERADOR") // Role que NÃO tem permissão no POST /autores
                .build();
        
        // Stub específico para esse token
        Mockito.when(jwtDecoder.decode("token-operador")).thenReturn(jwtOperador);

        // Act & Assert
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-operador") // Passamos o token sem permissão
                .body(payload)
            .when()
                .post("/autores")
            .then()
                .statusCode(403); // Valida que o Spring Security barrou na anotação @PreAuthorize
    }

    @Test
    @Order(3)
    @DisplayName("Salvar autor com dados nulos/vazios deve retornar 422")
    void deveRetornar422QuandoDadosIncompletosOuNulos() throws JsonProcessingException {
        // Arrange
        // Criando DTO faltando nome, data e nacionalidade (para estourar os @NotBlank e @NotNull)
        AutorDTO dtoInvalido = new AutorDTO(null, "", null, ""); 
        String payload = objectMapper.writeValueAsString(dtoInvalido);

        // Act & Assert
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // Token GERENTE válido
                .body(payload)
            .when()
                .post("/autores")
            .then()
                .statusCode(422); // Unprocessable Entity (Erro de validação)
    }

    @Test
    @Order(4)
    @DisplayName("Salvar autor duplicado deve retornar 409")
    void deveRetornar409QuandoAutorJaExistir() throws JsonProcessingException {
        // Arrange
        String payload = objectMapper.writeValueAsString(dto); 
        // Observação Fantástica: como o Teste 1 (Order 1) já salvou esse Machado de Assis 
        // de verdade no banco de dados do Testcontainers, e como nosso teste de integração
        // consulta o banco real, ao mandar os exatos mesmos dados, a Regra de Negócio 
        // do seu AutorService vai identificar a duplicidade na base e estourar a exceção!

        // Act & Assert
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // Token GERENTE Válido
                .body(payload)
            .when()
                .post("/autores")
            .then()
                .statusCode(409); // Conflict (Sua exceção de Registro Duplicado)
    }

    @Test
    @Order(5)
    @DisplayName("Obter detalhes de autor existente deve retornar 200")
    void deveObterDetalhesAutor() {
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
            .when()
                .get("/autores/" + idAutorCriado)
            .then()
                .statusCode(200)
                .body("nome", Matchers.equalTo(dto.nome()))
                .body("nacionalidade", Matchers.equalTo(dto.nacionalidade()));
    }

    @Test
    @Order(6)
    @DisplayName("Obter detalhes de autor inexistente deve retornar 404")
    void deveRetornar404AoObterDetalhesDeAutorInexistente() {
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
            .when()
                .get("/autores/" + UUID.randomUUID())
            .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("Atualizar autor deve retornar 204")
    void deveAtualizarAutor() throws JsonProcessingException {
        AutorDTO dtoAtualizacao = new AutorDTO(
                null,
                "Machado de Assis Atualizado",
                LocalDate.of(1839, 6, 21),
                "Brasileiro Atualizado"
        );
        String payload = objectMapper.writeValueAsString(dtoAtualizacao);

        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
                .body(payload)
            .when()
                .put("/autores/" + idAutorCriado)
            .then()
                .statusCode(204);
    }

    @Test
    @Order(8)
    @DisplayName("Atualizar autor inexistente deve retornar 404")
    void deveRetornar404AoAtualizarAutorInexistente() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(dto);

        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
                .body(payload)
            .when()
                .put("/autores/" + UUID.randomUUID())
            .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @DisplayName("Pesquisar autores deve retornar 200 com paginação")
    void devePesquisarAutores() {
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE ou OPERADOR
                .queryParam("nome", "Atualizado")
                .queryParam("pagina", 0)
                .queryParam("tamanho-pagina", 10)
            .when()
                .get("/autores")
            .then()
                .statusCode(200)
                .body("content", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1)))
                .body("content[0].nome", Matchers.containsString("Atualizado"));
    }

    @Test
    @Order(10)
    @DisplayName("Deletar autor inexistente deve retornar 404")
    void deveRetornar404AoDeletarAutorInexistente() {
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
            .when()
                .delete("/autores/" + UUID.randomUUID())
            .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    @DisplayName("Deletar autor deve retornar 204")
    void deveDeletarAutor() {
        RestAssured.given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer token-falso") // GERENTE
            .when()
                .delete("/autores/" + idAutorCriado)
            .then()
                .statusCode(204);
    }
}
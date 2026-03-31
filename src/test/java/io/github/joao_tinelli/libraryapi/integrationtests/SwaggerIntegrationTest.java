package io.github.joao_tinelli.libraryapi.integrationtests;

import io.github.joao_tinelli.libraryapi.config.TestConfigs;
import io.github.joao_tinelli.libraryapi.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Deve retornar Swagger UI")
    void testSwaggerUI(){
        var content = given()
                .basePath("/swagger-ui/index.html")
                .port(TestConfigs.SERVER_PORT)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        assertTrue(content.contains("Swagger UI"));
    }
}

package br.ifsp.demo.integration;

import br.ifsp.demo.annotation.ApiTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import br.ifsp.demo.annotation.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
@ApiTest
class TransactionControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/v1/hello")
    class Hello {

        @Test
        @DisplayName("should return 200 with user id when authenticated")
        void shouldReturn200WithUserIdWhenAuthenticated() {
            givenAuth()
                    .when().get("/api/v1/hello")
                    .then().statusCode(200)
                    .body(containsString("Hello:"));
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .when().get("/api/v1/hello")
                    .then().statusCode(401);
        }
    }
}

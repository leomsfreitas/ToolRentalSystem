package br.ifsp.demo.integration;

import br.ifsp.demo.annotation.ApiTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import br.ifsp.demo.annotation.IntegrationTest;
import br.ifsp.demo.integration.util.EntityBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
@ApiTest
class ToolControllerTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        toolRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/tools")
    class ListTools {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() {
            givenAuth()
                    .when().get("/api/v1/tools")
                    .then().statusCode(200)
                    .body("$", instanceOf(java.util.List.class));
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .when().get("/api/v1/tools")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("should include created tool in list")
        void shouldIncludeCreatedToolInList() {
            String name = "Hammer";
            givenAuth().body(EntityBuilder.createToolBody(name)).post("/api/v1/tools");

            givenAuth()
                    .when().get("/api/v1/tools")
                    .then().statusCode(200)
                    .body("name", hasItem(name));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/tools")
    class CreateTool {

        @Test
        @DisplayName("should create tool and return 201 with all fields")
        void shouldCreateToolAndReturn201() {
            String name = EntityBuilder.randomToolName();

            givenAuth()
                    .body(EntityBuilder.createToolBody(name))
                    .when().post("/api/v1/tools")
                    .then().statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo(name))
                    .body("status", equalTo("AVAILABLE"));
        }

        @Test
        @DisplayName("should return 400 when name is null")
        void shouldReturn400WhenNameIsNull() {
            givenAuth()
                    .body("{\"name\":null,\"dailyRate\":10.0,\"weeklyDailyRate\":8.0,\"monthlyDailyRate\":6.0}")
                    .when().post("/api/v1/tools")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when daily rate is null")
        void shouldReturn400WhenDailyRateIsNull() {
            givenAuth()
                    .body("{\"name\":\"Hammer\",\"dailyRate\":null,\"weeklyDailyRate\":8.0,\"monthlyDailyRate\":6.0}")
                    .when().post("/api/v1/tools")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .body(EntityBuilder.createToolBody())
                    .when().post("/api/v1/tools")
                    .then().statusCode(401);
        }
    }
}

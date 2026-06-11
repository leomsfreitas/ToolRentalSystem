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
class MaintenanceControllerTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        toolRepository.deleteAll();
    }

    private String createTool() {
        return givenAuth()
                .body(EntityBuilder.createToolBody())
                .post("/api/v1/tools")
                .then().statusCode(201)
                .extract().path("id");
    }

    @Nested
    @DisplayName("GET /api/v1/maintenance")
    class ListMaintenance {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() {
            givenAuth()
                    .when().get("/api/v1/maintenance")
                    .then().statusCode(200)
                    .body("$", instanceOf(java.util.List.class));
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .when().get("/api/v1/maintenance")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("should include tool after being sent to maintenance")
        void shouldIncludeToolAfterSentToMaintenance() {
            String toolId = createTool();
            givenAuth().post("/api/v1/maintenance/send/" + toolId);

            givenAuth()
                    .when().get("/api/v1/maintenance")
                    .then().statusCode(200)
                    .body("toolId", hasItem(toolId))
                    .body("closed", hasItem(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/maintenance/send/{toolId}")
    class SendToMaintenance {

        @Test
        @DisplayName("should send tool to maintenance and return 200")
        void shouldSendToolToMaintenanceAndReturn200() {
            String toolId = createTool();

            givenAuth()
                    .when().post("/api/v1/maintenance/send/" + toolId)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("should return 404 when tool does not exist")
        void shouldReturn404WhenToolDoesNotExist() {
            givenAuth()
                    .when().post("/api/v1/maintenance/send/non-existent-id")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 409 when tool is already in maintenance")
        void shouldReturn409WhenToolAlreadyInMaintenance() {
            String toolId = createTool();
            givenAuth().post("/api/v1/maintenance/send/" + toolId);

            givenAuth()
                    .when().post("/api/v1/maintenance/send/" + toolId)
                    .then().statusCode(409);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            String toolId = createTool();

            givenNoAuth()
                    .when().post("/api/v1/maintenance/send/" + toolId)
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/maintenance/return/{toolId}")
    class ReturnFromMaintenance {

        @Test
        @DisplayName("should return tool from maintenance and return 200")
        void shouldReturnToolFromMaintenanceAndReturn200() {
            String toolId = createTool();
            givenAuth().post("/api/v1/maintenance/send/" + toolId);

            givenAuth()
                    .when().put("/api/v1/maintenance/return/" + toolId)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("should return 404 when tool does not exist")
        void shouldReturn404WhenToolDoesNotExist() {
            givenAuth()
                    .when().put("/api/v1/maintenance/return/non-existent-id")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            String toolId = createTool();
            givenAuth().post("/api/v1/maintenance/send/" + toolId);

            givenNoAuth()
                    .when().put("/api/v1/maintenance/return/" + toolId)
                    .then().statusCode(401);
        }
    }
}

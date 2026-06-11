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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
@ApiTest
class RentalControllerTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        rentalRepository.deleteAll();
        toolRepository.deleteAll();
        customerRepository.deleteAll();
    }

    private String createCustomer() {
        return givenAuth()
                .body(EntityBuilder.createCustomerBody())
                .post("/api/v1/customers")
                .then().statusCode(201)
                .extract().path("id");
    }

    private String createTool() {
        return givenAuth()
                .body(EntityBuilder.createToolBody())
                .post("/api/v1/tools")
                .then().statusCode(201)
                .extract().path("id");
    }

    private String registerRental(String customerId, String toolId) {
        String body = String.format(
                "{\"customerId\":\"%s\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                customerId, toolId, LocalDate.now());
        return givenAuth().body(body).post("/api/v1/rentals")
                .then().statusCode(201)
                .extract().asString().replace("\"", "");
    }

    @Nested
    @DisplayName("GET /api/v1/rentals")
    class ListRentals {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() {
            givenAuth()
                    .when().get("/api/v1/rentals")
                    .then().statusCode(200)
                    .body("$", instanceOf(java.util.List.class));
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .when().get("/api/v1/rentals")
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/rentals")
    class RegisterRental {

        @Test
        @DisplayName("should register rental and return 201 with id")
        void shouldRegisterRentalAndReturn201() {
            String customerId = createCustomer();
            String toolId = createTool();
            String body = String.format(
                    "{\"customerId\":\"%s\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                    customerId, toolId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(201)
                    .body(not(emptyOrNullString()));
        }

        @Test
        @DisplayName("should return 404 when customer does not exist")
        void shouldReturn404WhenCustomerDoesNotExist() {
            String toolId = createTool();
            String body = String.format(
                    "{\"customerId\":\"non-existent\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                    toolId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 404 when tool does not exist")
        void shouldReturn404WhenToolDoesNotExist() {
            String customerId = createCustomer();
            String body = String.format(
                    "{\"customerId\":\"%s\",\"toolIds\":[\"non-existent\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                    customerId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 409 when tool is already rented")
        void shouldReturn409WhenToolIsAlreadyRented() {
            String customerId = createCustomer();
            String toolId = createTool();
            registerRental(customerId, toolId);

            String body = String.format(
                    "{\"customerId\":\"%s\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                    customerId, toolId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(409);
        }

        @Test
        @DisplayName("should return 409 when tool is in maintenance")
        void shouldReturn409WhenToolIsInMaintenance() {
            String customerId = createCustomer();
            String toolId = createTool();
            givenAuth().post("/api/v1/maintenance/send/" + toolId);

            String body = String.format(
                    "{\"customerId\":\"%s\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":\"CASH_DEPOSIT\"}",
                    customerId, toolId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(409);
        }

        @Test
        @DisplayName("should return 400 when guarantee type is null")
        void shouldReturn400WhenGuaranteeTypeIsNull() {
            String customerId = createCustomer();
            String toolId = createTool();
            String body = String.format(
                    "{\"customerId\":\"%s\",\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"guaranteeType\":null}",
                    customerId, toolId, LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals")
                    .then().statusCode(400);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/rentals/{id}/finalize")
    class FinalizeRental {

        @Test
        @DisplayName("should finalize rental and return 200 with total value")
        void shouldFinalizeRentalAndReturn200() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenAuth()
                    .body(String.format("{\"endDate\":\"%s\"}", LocalDate.now().plusDays(3)))
                    .when().put("/api/v1/rentals/" + rentalId + "/finalize")
                    .then().statusCode(200)
                    .body(notNullValue());
        }

        @Test
        @DisplayName("should return 404 when rental does not exist")
        void shouldReturn404WhenRentalDoesNotExist() {
            givenAuth()
                    .body(String.format("{\"endDate\":\"%s\"}", LocalDate.now().plusDays(1)))
                    .when().put("/api/v1/rentals/non-existent/finalize")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 409 when rental is already finalized")
        void shouldReturn409WhenRentalAlreadyFinalized() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenAuth()
                    .body(String.format("{\"endDate\":\"%s\"}", LocalDate.now().plusDays(2)))
                    .put("/api/v1/rentals/" + rentalId + "/finalize");

            givenAuth()
                    .body(String.format("{\"endDate\":\"%s\"}", LocalDate.now().plusDays(3)))
                    .when().put("/api/v1/rentals/" + rentalId + "/finalize")
                    .then().statusCode(409);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenNoAuth()
                    .body(String.format("{\"endDate\":\"%s\"}", LocalDate.now().plusDays(1)))
                    .when().put("/api/v1/rentals/" + rentalId + "/finalize")
                    .then().statusCode(401);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/rentals/{id}/cancel")
    class CancelRental {

        @Test
        @DisplayName("should cancel rental and return 204")
        void shouldCancelRentalAndReturn204() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenAuth()
                    .when().put("/api/v1/rentals/" + rentalId + "/cancel")
                    .then().statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when rental does not exist")
        void shouldReturn404WhenRentalDoesNotExist() {
            givenAuth()
                    .when().put("/api/v1/rentals/non-existent/cancel")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenNoAuth()
                    .when().put("/api/v1/rentals/" + rentalId + "/cancel")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("should return 409 when rental is already cancelled")
        void shouldReturn409WhenRentalAlreadyCancelled() {
            String customerId = createCustomer();
            String toolId = createTool();
            String rentalId = registerRental(customerId, toolId);

            givenAuth().put("/api/v1/rentals/" + rentalId + "/cancel");

            givenAuth()
                    .when().put("/api/v1/rentals/" + rentalId + "/cancel")
                    .then().statusCode(409);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/rentals/query-value")
    class QueryRentalValue {

        @Test
        @DisplayName("should return 200 with estimated value")
        void shouldReturn200WithEstimatedValue() {
            String toolId = createTool();
            String body = String.format(
                    "{\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"endDate\":\"%s\"}",
                    toolId, LocalDate.now(), LocalDate.now().plusDays(3));

            givenAuth().body(body)
                    .when().post("/api/v1/rentals/query-value")
                    .then().statusCode(200)
                    .body(notNullValue());
        }

        @Test
        @DisplayName("should return 404 when tool does not exist")
        void shouldReturn404WhenToolDoesNotExist() {
            String body = String.format(
                    "{\"toolIds\":[\"non-existent\"],\"startDate\":\"%s\",\"endDate\":\"%s\"}",
                    LocalDate.now(), LocalDate.now().plusDays(3));

            givenAuth().body(body)
                    .when().post("/api/v1/rentals/query-value")
                    .then().statusCode(404);
        }

        @Test
        @DisplayName("should return 400 when end date is before start date")
        void shouldReturn400WhenEndDateIsBeforeStartDate() {
            String toolId = createTool();
            String body = String.format(
                    "{\"toolIds\":[\"%s\"],\"startDate\":\"%s\",\"endDate\":\"%s\"}",
                    toolId, LocalDate.now().plusDays(5), LocalDate.now());

            givenAuth().body(body)
                    .when().post("/api/v1/rentals/query-value")
                    .then().statusCode(400);
        }
    }
}

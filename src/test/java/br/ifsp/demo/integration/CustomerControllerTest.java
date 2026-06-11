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
class CustomerControllerTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/customers")
    class ListCustomers {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() {
            givenAuth()
                    .when().get("/api/v1/customers")
                    .then().statusCode(200)
                    .body("$", instanceOf(java.util.List.class));
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .when().get("/api/v1/customers")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("should include created customer in list")
        void shouldIncludeCreatedCustomerInList() {
            String name = "Alice Smith";
            givenAuth().body(EntityBuilder.createCustomerBody(name)).post("/api/v1/customers");

            givenAuth()
                    .when().get("/api/v1/customers")
                    .then().statusCode(200)
                    .body("name", hasItem(name));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/customers")
    class CreateCustomer {

        @Test
        @DisplayName("should create customer and return 201 with id and name")
        void shouldCreateCustomerAndReturn201() {
            String name = EntityBuilder.randomCustomerName();

            givenAuth()
                    .body(EntityBuilder.createCustomerBody(name))
                    .when().post("/api/v1/customers")
                    .then().statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo(name));
        }

        @Test
        @DisplayName("should return 400 when name is null")
        void shouldReturn400WhenNameIsNull() {
            givenAuth()
                    .body("{\"name\":null}")
                    .when().post("/api/v1/customers")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            givenNoAuth()
                    .body(EntityBuilder.createCustomerBody())
                    .when().post("/api/v1/customers")
                    .then().statusCode(401);
        }
    }
}

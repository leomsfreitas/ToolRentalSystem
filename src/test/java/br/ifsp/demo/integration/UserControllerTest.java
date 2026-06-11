package br.ifsp.demo.integration;

import br.ifsp.demo.annotation.ApiTest;
import br.ifsp.demo.annotation.IntegrationTest;
import br.ifsp.demo.integration.util.EntityBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
@ApiTest
class UserControllerTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("POST /api/v1/register")
    class Register {

        @Test
        @DisplayName("should register user and return 201 with id")
        void shouldRegisterUserAndReturn201WithId() {
            given()
                    .contentType("application/json")
                    .body(EntityBuilder.registerUserBody())
                    .when().post("/api/v1/register")
                    .then().statusCode(201)
                    .body("id", notNullValue());
        }

        @Test
        @DisplayName("should return 409 when email is already registered")
        void shouldReturn409WhenEmailAlreadyRegistered() {
            String email = EntityBuilder.randomEmail();
            String body = EntityBuilder.registerUserBody(email, "Test@1234");

            given().contentType("application/json").body(body).post("/api/v1/register");

            given().contentType("application/json").body(body)
                    .when().post("/api/v1/register")
                    .then().statusCode(409);
        }

        @Test
        @DisplayName("should return 400 when name is null")
        void shouldReturn400WhenNameIsNull() {
            String body = String.format(
                    "{\"name\":null,\"lastname\":\"%s\",\"email\":\"%s\",\"password\":\"Test@1234\"}",
                    EntityBuilder.randomLastName(), EntityBuilder.randomEmail());

            given().contentType("application/json").body(body)
                    .when().post("/api/v1/register")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when email is null")
        void shouldReturn400WhenEmailIsNull() {
            String body = String.format(
                    "{\"name\":\"%s\",\"lastname\":\"%s\",\"email\":null,\"password\":\"Test@1234\"}",
                    EntityBuilder.randomFirstName(), EntityBuilder.randomLastName());

            given().contentType("application/json").body(body)
                    .when().post("/api/v1/register")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when password is null")
        void shouldReturn400WhenPasswordIsNull() {
            String body = String.format(
                    "{\"name\":\"%s\",\"lastname\":\"%s\",\"email\":\"%s\",\"password\":null}",
                    EntityBuilder.randomFirstName(), EntityBuilder.randomLastName(), EntityBuilder.randomEmail());

            given().contentType("application/json").body(body)
                    .when().post("/api/v1/register")
                    .then().statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when email format is invalid")
        void shouldReturn400WhenEmailFormatIsInvalid() {
            String body = String.format(
                    "{\"name\":\"%s\",\"lastname\":\"%s\",\"email\":\"not-an-email\",\"password\":\"Test@1234\"}",
                    EntityBuilder.randomFirstName(), EntityBuilder.randomLastName());

            given().contentType("application/json").body(body)
                    .when().post("/api/v1/register")
                    .then().statusCode(400);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/authenticate")
    class Authenticate {

        @Test
        @DisplayName("should authenticate and return JWT token")
        void shouldAuthenticateAndReturnToken() {
            given()
                    .contentType("application/json")
                    .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", TEST_EMAIL, TEST_PASSWORD))
                    .when().post("/api/v1/authenticate")
                    .then().statusCode(200)
                    .body("token", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("should return 401 when password is wrong")
        void shouldReturn401WhenPasswordIsWrong() {
            given()
                    .contentType("application/json")
                    .body(String.format("{\"username\":\"%s\",\"password\":\"wrongpassword\"}", TEST_EMAIL))
                    .when().post("/api/v1/authenticate")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("should return 401 when user does not exist")
        void shouldReturn401WhenUserDoesNotExist() {
            given()
                    .contentType("application/json")
                    .body(String.format("{\"username\":\"%s\",\"password\":\"Test@1234\"}", EntityBuilder.randomEmail()))
                    .when().post("/api/v1/authenticate")
                    .then().statusCode(401);
        }
    }
}

package br.ifsp.demo.integration;

import br.ifsp.demo.infrastructure.persistence.jpa.CustomerJpaRepository;
import br.ifsp.demo.infrastructure.persistence.jpa.RentalJpaRepository;
import br.ifsp.demo.infrastructure.persistence.jpa.ToolJpaRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    protected String authToken;

    @Autowired protected JpaUserRepository userRepository;
    @Autowired protected CustomerJpaRepository customerRepository;
    @Autowired protected ToolJpaRepository toolRepository;
    @Autowired protected RentalJpaRepository rentalRepository;

    protected static final String TEST_EMAIL = "integration@test.com";
    protected static final String TEST_PASSWORD = "Test@1234";

    @BeforeEach
    void setUpBase() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        authToken = registerAndAuthenticate(TEST_EMAIL, TEST_PASSWORD);
    }

    @AfterEach
    void tearDownBase() {
        userRepository.deleteAll();
    }

    protected String registerAndAuthenticate(String email, String password) {
        given().contentType(ContentType.JSON)
                .body(String.format(
                        "{\"name\":\"Test\",\"lastname\":\"User\",\"email\":\"%s\",\"password\":\"%s\"}",
                        email, password))
                .post("/api/v1/register");

        return given().contentType(ContentType.JSON)
                .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", email, password))
                .post("/api/v1/authenticate")
                .then().extract().path("token");
    }

    protected RequestSpecification givenAuth() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken);
    }

    protected RequestSpecification givenNoAuth() {
        return RestAssured.given()
                .contentType(ContentType.JSON);
    }
}

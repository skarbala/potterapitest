package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class CharacterTest {

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/characters";
    }

    @Test
    void itShouldReturnErrorMessageWhenUserIsNotAuthenticated() {
        when().get()
                .then()
                .statusCode(401)
                .body("message", equalTo("Sorry Wizard, can't let you in."));
    }

    @Test
    void itShouldReturnCharactersWhenUserIsAuthenticated() {
        given().auth()
                .preemptive()
                .basic("admin", "supersecret")
                .when().get().then().statusCode(200);
    }
}

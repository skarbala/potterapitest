package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class HousesTest {

    private static String token;

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        token = given().auth().preemptive().basic("admin", "supersecret")
                .when().get("/login")
                .then().extract().jsonPath().get("token");
    }

    @Test
    void itShouldReturnErrorMessageWithoutToken() {
        when().get("/houses")
                .then().statusCode(403)
                .and().body("message", equalTo("Sorry Wizard you dont have TOKEN"));
    }

    @Test
    void itShouldReturnHousesWithToken() {
        given()
                .header("Authorization", "Bearer ".concat(token))
                .when().get("/houses").then().statusCode(200);
    }

    @Test
    void itShouldReturnNameForEachCharacter() {
        given().header("Authorization", "Bearer ".concat(token))
                .when().get("houses/5a05e2b252f721a3cf2ea33f")
                .then().extract().response().jsonPath()
                .getList("members", String.class)
                .forEach(id -> {
                    given().pathParam("id", id)
                            .auth()
                            .preemptive()
                            .basic("admin", "supersecret")
                            .when().get("/characters/{id}")
                            .then().statusCode(200)
                            .body("name", not(emptyOrNullString()))
                            .body("house", equalTo("Gryffindor"));
                });
    }

    @Test
    void itShouldReturnSpecificHouseWithToken() {

    }

    @Test
    void itShouldReturnErrorMessageForSpecificHouseWithoutToken() {


    }
}

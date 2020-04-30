package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class HousesTest {

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void itShouldReturnErrorMessageWithoutToken() {
        when().get("/houses")
                .then().statusCode(403)
                .and().body("message",equalTo("Sorry Wizard you dont have TOKEN"));
    }

    @Test
    void itShouldReturnHousesWhenUserHasToken() {
        //1 request - ziskam token
        String token = given().auth().preemptive().basic("admin","supersecret")
                .when().get("/login")
                .then().extract().jsonPath().get("token");
        //2 request pouzijem token a dotiahnem houses
        given()
                .header("Authorization","Bearer ".concat(token))
                .when().get("/houses").then().statusCode(200);
    }
}

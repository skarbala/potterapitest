package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class SortingHatTest {

    @BeforeAll
    static void config(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port= 3000;
        RestAssured.basePath = "sortingHat";
    }

    @Test
    void itShouldReturnStatusCode200() {
        when().get().then().statusCode(200);
    }

    @Test
    void itShouldReturnCorrectKeys() {
        when().get()
                .then()
                .body("", hasKey("sortingHatSays"))
                .body("", hasKey("house"));
    }

    @Test
    void itShouldContainOneOfHouses() {
        when().get().then().body("house", oneOf("Slytherin", "Ravenclaw", "Gryffindor", "Hufflepuff"));
    }

    @Test
    void itShouldContainMessageFromSortingHat() {
        when().get()
                .then()
                .body("sortingHatSays", not(emptyOrNullString()));
    }
}

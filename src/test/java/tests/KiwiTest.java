package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.time.OffsetDateTime.now;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;

public class KiwiTest {

    private static final String BASE_URL = "https://api.skypicker.com";

    @BeforeAll
    static void config() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void itShouldReturnLocationBasedOnTerm() {
        String[] expectedStations = new String[]{
                "Praha hlavní nádraží",
                "Praha - Masarykovo ",
                "Praha-Kbely", "Praha-Smíchov ",
                "Prague Holesovice (Praha)"
        };
        given().queryParam("term", "Praha")
                .queryParam("location_types", "station")
                .queryParam("limit", 10)
                .queryParam("active_only", true)
                .when().get("/locations")
                .then().statusCode(200)
                .and().body("locations.name", hasItems(expectedStations))
                .and().body(matchesJsonSchemaInClasspath("locations.json"))
                .extract().path("locations.name");
    }

    @Test
    void jsonPathShowtime() {
        //najdi vsetky special miesta v Tokyo
        given().queryParam("type", "id")
                .queryParam("id", "PRG")
                .when().get("/locations")
                .then().statusCode(200)
                .extract().jsonPath().getList("locations[0].special.name")
                .forEach(System.out::println);
    }

    //najdite prahu ktora je v strednej europe a vrat mi jeho ID
    @Test
    void jsonPathShowTime2() {
        given().queryParam("term", "Praha")
                .queryParam("location_types", "city")
                .when().get("/locations")
                .then().statusCode(200)
                .extract().jsonPath().get("locations.find{locations-> locations.continent.name=='Europe'}.id");
    }

    //top locations from tokyo
    //najdi let praha-tokyo
    //over ze odpoved pride do 5 sekund
    @Test
    void itShouldFindFlightFromPRGToHND() {
        String flyFrom = getIdOfCity("Wien");
        String flyTo = getIdOfCity("Wellington");
        given().queryParam("flyFrom", flyFrom)
                .queryParam("to", flyTo)
                .queryParam("dateFrom", getDateWithOffset(1))
                .queryParam("dateTo", getDateWithOffset(20))
                .queryParam("partner", "picky")
                .when().get("/flights")
                .then()
                .statusCode(200)
                .time(lessThan(6000L));
    }

    private static String getDateWithOffset(int offset) {
        return now()
                .plusDays(offset)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getIdOfCity(String term) {
        return given().baseUri(BASE_URL).queryParam("term", term)
                .queryParam("location_types", "airport")
                .queryParam("limit", 1)
                .queryParam("active_only", true)
                .when().get("/locations")
                .then().statusCode(200)
                .extract().path("locations[0].id");
    }

}

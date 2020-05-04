package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static java.time.OffsetDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class KiwiWorkshopTest {

    private static final String BASE_URL = "https://api.skypicker.com";

    @BeforeAll
    static void config() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void itShouldReturnAllStationsInPrague() {
        String[] expectedStations = new String[]{
                "Praha hlavní nádraží",
                "Praha - Masarykovo ",
                "Praha-Kbely",
                "Praha-Smíchov ",
                "Prague Holesovice (Praha)"
        };

        given().queryParam("term", "Praha")
                .queryParam("location_types", "station","city")
                .when().get("/locations")
                .then().statusCode(200)
                .body("locations.name", hasItems(expectedStations));
    }

    @Test
    void itShouldListAllSpecialPlacesNearTokyo() {
        List<String> specialLocations = given().queryParam("term", "Tokyo")
                .queryParam("location_types", "airport")
                .queryParam("limit",1)
                .when().get("/locations")
                .then().statusCode(200)
                .extract().jsonPath().getList("locations[0].special.name");

        assertThat(specialLocations, is(not(empty())));
    }

    @Test
    void itShouldFindFlightFromVIEtoWLG() {
       String url =  given().queryParam("flyFrom",getIdOfCity("Wien"))
                .queryParam("to",getIdOfCity("Tokyo"))
                .queryParam("dateFrom",getDateWithOffset(1))
                .queryParam("dateTo",getDateWithOffset(100))
                .queryParam("partner","picky")
                .queryParam("limit",1)
        .when().get("/flights")
                .then().statusCode(200)
                .time(lessThan(5000L)).extract().jsonPath()
                .get("data[0].deep_link");
       open(url);
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

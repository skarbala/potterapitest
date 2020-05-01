package tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;

public class KiwiTest {

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "https://api.skypicker.com";
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
                .extract().path("locations.name") ;}

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
        given().queryParam("flyFrom", "PRG")
                .queryParam("to", "HND")
                .queryParam("dateFrom", getRandomDate(1))
                .queryParam("dateTo", getRandomDate(50))
                .queryParam("partner", "picky")
                .when().get("/flights")
                .then()
                .statusCode(200)
                .time(lessThan(5000L));
    }

    private String getRandomDate(int offsetFromToday) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Faker().date().future(offsetFromToday, TimeUnit.DAYS));
    }
}

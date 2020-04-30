package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpellsTest {

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/spells";
    }

    @Test
    void itShouldReturnOneSpecificSpell() {
        given().pathParam("spellId", "5b74ee1d3228320021ab6239")
                .when().get("/{spellId}")
                .then().statusCode(200).statusLine(containsString("OK"))
                .body("spell", equalTo("Avada Kedavra"))
                .body("effect", equalTo("murders opponent"))
                .body("type", equalTo("Curse"))
                .body("isUnforgivable", is(true));
    }

    @Test
    void itShouldReturnErrorMessageWhenSpellIsNotFound() {
        given().pathParam("spellId", "invalid")
                .when().get("/{spellId}")
                .then().log().status().body("message", equalTo("Spell not found"))
                .statusCode(404);
    }

    @Test
    void itShouldReturnSpellForEachSpellInList() {
        List<HashMap<Object, String>> spells =
                when().get()
                        .then().extract().response()
                        .jsonPath().getList("$.isUnforgivable");

        spells.forEach(spell -> {
            assertThat(spell.get("effect"), not(emptyOrNullString()));
            assertThat(spell.get("spell"), not(emptyOrNullString()));
            assertThat(spell.get("id"), not(emptyOrNullString()));
        });
    }

    @Test
    void itShouldReturnExactNumberOfSpells() {
        int size = when().get().then().extract().response().jsonPath().getList("$").size();
        assertThat(size, is(151));
    }

    @Test
    void itShouldContainAvadaKedavra() {
       when().get().then().extract().response().jsonPath().getList("$.spell").size();

    }
}

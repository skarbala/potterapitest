package tests;

import com.github.javafaker.Faker;
import exceptions.SpellNotFoundException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Spell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.stream.Collectors.toList;
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
        List<Spell> spells =
                when().get()
                        .then().extract().response()
                        .jsonPath().getList("$", Spell.class);

        spells.forEach(spell -> {
            assertThat(spell.getEffect(), not(emptyOrNullString()));
            assertThat(spell.getSpell(), not(emptyOrNullString()));
            assertThat(spell.getId(), not(emptyOrNullString()));
        });
    }

    @Test
    void itShouldReturnExactNumberOfSpells() {
        int size = when().get().then().extract().response().jsonPath().getList("$").size();
        assertThat(size, greaterThanOrEqualTo(151));
    }

    @Test
    void itShouldContainAvadaKedavra() {
        when().get().then().extract().response().jsonPath().getList("$.spell").size();
    }

    @Test
    void itShouldContainSpecificSpells() {
        String[] expectedSpells = new String[]{"Avada Kedavra", "Crucio", "Imperio"};
        List<String> actualSpells = when().get().then().extract().jsonPath().getList("spell");
        assertThat(actualSpells, hasItems(expectedSpells));
    }

    @Test
    void statusLineShouldContainOK() {
        when().get().then().log().status().statusLine(containsString("OK"));
    }

    @Test
    void itShouldContainCurseSpells() {
        List<Spell> spells = when().get().then().extract().jsonPath().getList("$", Spell.class);

        spells = spells.stream().filter(object -> object.getType().equals("Curse"))
                .collect(toList());
        assertThat(spells, hasSize(greaterThan(10)));
    }

    @Test
    void itShouldFindSpellIdAndSendItAsParameter() throws SpellNotFoundException {
        String spellToFind = "Avada Kedavra";

        List<Spell> spells = when().get().then().extract().jsonPath().getList("$", Spell.class);
        // ziskam ID
        String id = spells.stream().filter(spell -> spell.getSpell().equals(spellToFind))
                .findFirst()
                .orElseThrow(() -> new SpellNotFoundException(spellToFind))
                .getId();
        // id pouzijem ako path parameter
        given().pathParam("spellId", id)
                .when().get("/{spellId}")
                .then().statusCode(200).statusLine(containsString("OK"))
                .body("spell", equalTo(spellToFind))
                .body("effect", equalTo("murders opponent"))
                .body("type", equalTo("Curse"))
                .body("isUnforgivable", is(true));
    }

    @Test
    void itShouldFilterSpellsBasedOnQueryType() {
        List<Spell> spells = given().queryParam("type", "Curse")
                .when().get().then().extract().jsonPath().getList("$", Spell.class);

        spells.forEach(spell -> assertThat(spell.getType(), equalTo("Curse")));
        assertThat(spells, hasSize(greaterThan(10)));
    }

    @Test
    void itShouldAddNewSpell() {
        Faker faker = new Faker();
        Spell spell = new Spell(
                "Corona".concat(faker.letterify("????")),
                "Curse",
                "sneezing forever",
                true
        );
        //2 pomocou POST poslem toto kuzlo serveru
        given().contentType(ContentType.JSON)
                .body(spell)
                .when().post()
                .then()
                .statusCode(201)
                .body("message", equalTo("Spell created"))
                .body("spell.id", not(emptyOrNullString()));
    }
}

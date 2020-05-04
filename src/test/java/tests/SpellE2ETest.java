package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.when;

public class SpellE2ETest {

    @BeforeAll
    static void config() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
        RestAssured.basePath = "/spells";
        Configuration.holdBrowserOpen=true;
    }

    @Test
    void itShouldDisplayMischiefManagedTitleWhenListOfSpellsIsEmpty() {
        //vymazem kuzla
        when().get("/actions/deleteAll").then().statusCode(200);
        //otvorim stranku
        open("http://localhost:8080/#/spelleology");
        //overim title
        $("h1.subtitle")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Mischief managed"));
    }
}

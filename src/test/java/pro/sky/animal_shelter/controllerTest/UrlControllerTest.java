package pro.sky.animal_shelter.controllerTest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
@SpringBootTest
public class UrlControllerTest {
    private final String token = "807035350:AAHHsHwslk9_SKuqLLqpJbKuhQoOAipa12U";
    @Test
    void successSendMessage(){
        RestAssured.baseURI = "https://api.telegram.org/bot" + token;
        given()
                .param("text","test_message")
                .param("chat_id","982721415")
                .when()
                .get("/sendMessage")
                .then()
                .statusCode(200);
    }
}

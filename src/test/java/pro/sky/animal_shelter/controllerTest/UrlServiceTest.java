package pro.sky.animal_shelter.controllerTest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
@SpringBootTest
public class UrlServiceTest {
    /**
     * Проверяем метод отправки сообщения телеграм ботом, если при отправке приходит http ответ с кодом 200,
     * то тест пройден
     */
    @Test
    void successSendMessage(){
        RestAssured.baseURI = "https://api.telegram.org/bot807035350:AAHHsHwslk9_SKuqLLqpJbKuhQoOAipa12U";
        given()
                .param("text","test_message")
                .param("chat_id","982721415")
                .when()
                .get("/sendMessage")
                .then()
                .statusCode(200);
    }

}

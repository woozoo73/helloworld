package foo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    int port;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @Test
    void postGreeting() throws JsonProcessingException {
        Greeting requestGreeting = new Greeting();
        requestGreeting.setId(7);
        requestGreeting.setContent("Foo");

        String json = objectMapper.writeValueAsString(requestGreeting);
        String key = EncryptUtils.generateKey();
        String value = EncryptUtils.encrypt(json, key);

        given().log().all()
                .when().contentType(ContentType.URLENC)
                .formParam("value", value)
                .formParam("key", key)
                .post("/greeting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void getGreeting() {
        given().log().all()
                .get("/greeting")
                .then().log().all()
                .statusCode(200);
    }

}

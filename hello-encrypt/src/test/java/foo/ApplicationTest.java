package foo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
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
        RestAssured.config = new RestAssuredConfig().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"));
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
                .formParam("p", value)
                .formParam("q", key)
                .post("/greeting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void getGreeting() {
        given().log().all()
                .when().contentType(ContentType.URLENC)
                .get("/greeting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void getNothing() {
        given().log().all()
                .when().contentType(ContentType.URLENC)
                .get("/nothing")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void getPathAndParam() {
        given().log().all()
                .when().contentType(ContentType.URLENC)
                .param("param", "bar")
                .get("/path-and-param/" + "foo")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void postForm() {
        given().log().all()
                .when().contentType(ContentType.URLENC)
                .param("id", "9")
                .param("content", "form-value")
                .post("/form/")
                .then().log().all()
                .statusCode(200);
    }

}

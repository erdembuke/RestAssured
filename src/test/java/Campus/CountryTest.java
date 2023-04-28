package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {
    Faker faker = new Faker();
    RequestSpecification requestSpecification;
    String countryID;
    String countryName;

    @BeforeClass
    public void login(){
        baseURI = "https://test.mersys.io";
        Map<String,String> userCredential = new HashMap<>();
        userCredential.put("username" , "turkeyts");
        userCredential.put("password" , "TechnoStudy123");
        userCredential.put("rememberMe" , "true");
        Cookies cookies =
        given()
                .contentType(ContentType.JSON)
                .body(userCredential)

                .when()
                .post("/auth/login")

                .then()
                // .log().all()
                .statusCode(200)
                .extract().response().getDetailedCookies()
                ;
        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createCountry(){
        Map<String,String> countryInfo = new HashMap<>();
        countryName = faker.address().country() + faker.number().digits(3);
        countryInfo.put("name" , countryName);
        countryInfo.put("code" , faker.address().countryCode());

        countryID =
        given()
                .spec(requestSpecification)
                .body(countryInfo)
                .log().body()

                .when()
                .post("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
                ;
        System.out.println("countryID = " + countryID);

    }

    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative(){
        Map<String,String> map = new HashMap<>();
        map.put("id" , countryID);
        map.put("name" , countryName);
        map.put("code" , faker.address().countryCode() + faker.number().digits(5));

        given()
                .spec(requestSpecification)
                .body(map)

                .when()
                .post("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
                .body("message" , containsString("already"))
                ;
    }

    @Test(dependsOnMethods = "createCountryNegative")
    public void updateCountry(){
        Map<String,String> map = new HashMap<>();
        map.put("id" , countryID);

        countryName = "erdembuke"+faker.number().digits(7);
        map.put("name" , countryName);
        map.put("code" , faker.address().countryCode() + faker.number().digits(5));

        given()
                .spec(requestSpecification)
                .body(map)

                .when()
                .put("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                .body("name" , equalTo(countryName))
        ;

    }

    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountry(){

        given()
                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/countries/" + countryID)

                .then()
                .log().body()
                .statusCode(200)
                ;
    }

    @Test(dependsOnMethods = "deleteCountry")
    public void deleteCountryNegative(){
        given()
                .spec(requestSpecification)
                .pathParam("countryID" , countryID)

                .when()
                .delete("/school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)
        ;

    }

}

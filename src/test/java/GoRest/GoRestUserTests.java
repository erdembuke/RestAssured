package GoRest;
import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class GoRestUserTests {
    Faker faker = new Faker();
    int userID;
    RequestSpecification reqSpec;

    @BeforeClass
    public void setup() {

        baseURI = "https://gorest.co.in/public/v2/users";
        //baseURI ="https://test.gorest.co.in/public/v2/users/";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer e4b22047188da067d3bd95431d94259f63896347f9864894a0a7013ee5f9c703")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();

    }

    @Test(enabled = false)
    public void createUserJSON(){
        // https://gorest.co.in/public/v2/users
        // "Authorization : Bearer ecbc9f85c920e90c4c2e40a08075cb15d13777492b2e3a9659cd883170c91e83"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}

        String rndFullName = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        userID =
        given()
                .spec(reqSpec)
                .body("{\"name\":\"" + rndFullName + "\", \"gender\":\"male\", \"email\":\"" + rndEmail + "\", \"status\":\"active\"}")
                //.log().uri()
                //.log().body()

                .when()
                .post("")

                .then()
                //.log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().path("id")
                ;
    }

    @Test
    public void createUserMAP(){

        String rndFullName = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        Map<String,String> newUser = new HashMap<>();
        newUser.put("name",rndFullName);
        newUser.put("gender" , "male");
        newUser.put("email" , rndEmail);
        newUser.put("status", "active");

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }

    @Test(enabled = false)
    public void createUserObject(){

        String rndFullName = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        User newuser = new User();
        newuser.name = rndFullName;
        newuser.gender = "male";
        newuser.email = rndEmail;
        newuser.status = "active";

        userID =
                given()
                        .spec(reqSpec)
                        .body(newuser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }



    @Test(dependsOnMethods = "createUserMAP")
    public void getUserByID(){

        given()
                .spec(reqSpec)

                .when()
                .get(String.valueOf(userID))

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id" , equalTo(userID))
                ;
    }





    @Test(dependsOnMethods = "createUserMAP")
    public void updateUser(){

        Map<String,String> map = new HashMap<>();
        map.put("name" , "erdembke77");

        given()
                .spec(reqSpec)
                .body(map)

                .when()
                .put(String.valueOf(userID))

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name" , equalTo(map.get("name")))
        ;

    }





    @Test(dependsOnMethods = "updateUser")
    public void deleteUser(){

        given()
                .spec(reqSpec)

                .when()
                .delete("" + userID)

                .then()
                .log().body()
                .statusCode(204)
                ;
    }





    @Test(dependsOnMethods = "deleteUser")
    public void deleteUserNegative(){

        given()
                .spec(reqSpec)

                .when()
                .delete("" + userID)

                .then()
                .log().all()
                .statusCode(404)
                ;
    }
}

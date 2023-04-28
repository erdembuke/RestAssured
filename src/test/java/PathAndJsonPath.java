import GoRest.User;
import Model.Location;
import Model.Place;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PathAndJsonPath {

    @Test
    public void extractingPath(){
        String postCode =
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .extract().path("'post code'")
                ;

        System.out.println(postCode);
    }

    @Test
    public void extractingJsonPath(){
        int postCode =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().jsonPath().getInt("'post code'")
                ;

        System.out.println("post code -> " + postCode);
    }

    @Test
    public void getUsers(){
        Response response =
        given()

                .when()
                .get("https://gorest.co.in/public/v2/users")

                .then()
                //.log().body()
                .extract().response()
                ;

        int pathId = response.path("[2].id");
        int pathJsonId = response.jsonPath().getInt("[2].id");
        System.out.println("pathId = " + pathId);
        System.out.println("pathJsonId = " + pathJsonId);

        User[] usersPath = response.as(User[].class); // nesne donusumunda (POJO) dizi destekli
        List<User> userListJsonPath = response.jsonPath().getList("", User.class);

        System.out.println("usersPath -> " + Arrays.toString(usersPath));
        System.out.println("userListJsonPath = " + userListJsonPath);

    }

    @Test
    public void getUsersV1(){
        Response response =
        given()


                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .extract().response()
                ;

        List<User> dataUsers = response.jsonPath().getList("data", User.class);
        // JSONPATH bir response icindeki bir parcayi nesneye donusturebiliriz
        System.out.println("dataUsers = " + dataUsers);

        // daha onceki orneklerde (as) Clas donusumleri icin tum yapiya karsilik gelen
        // gereken tum classlari yazarak donusturup istedigimiz elemanlara ulasiyorduk
        // burada ise (jsonpath) aradaki bir veriyi clasa donusturerek bir list olarak almamiza
        // imkan veren JSONPATH i kullandik. boylece tek class ise veri alinmis oldu
        // diger classlara gerek kalmadan

        // path : class veya tip donusumune imkan vermeyen direkt veriyi verir. List<String> gibi
        // jsonPath : class donusumune ve tip donusumune izin vererek , veriyi istedigimiz formatta verir.

        // SADECE ALACAGIMIZ YERİN CLASS I OLMASI YETERLI
        // PATH ILE IKI AYRI CLASS OLUSTURUP ARRAY YAPMAMIZ GEREKCEKTI

    }

    @Test
    public void getZipCode(){
        Response response =
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .extract().response()
                ;

        List<Place> userList = response.jsonPath().getList("places" , Place.class);
        System.out.println("userList = " + userList);
    }


}

import Model.Location;
import Model.Place;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class ZippoTest {

    @Test
    public void Test1() {

        given()
                // preparation , hazirlik islemleri : (token,send body, parametreler)
                .when()
                // endpoint (url) , metodu (post,get,delete...)
                .then()
        // assertion, test, data islemleri
        ;

    }

    @Test
    public void checkCountryInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // donen body json data si , hepsini gostermek icin log.all()
                .statusCode(200) // donus kodu 200 mu
                .body("country", equalTo("United States")) // body nin country degiskeni "United States" esit mi
        // pm.response.json().id -> body.id
        ;
    }
    /*
PM                            RestAssured
body.country                  body("country")
body.'post code'              body("post code")
body.places[0].'place name'   body("places[0].'place name'")
body.places.'place name'      body("places.'place name'")
bütün place nameleri bir arraylist olarak verir

https://jsonpathfinder.com/
 */

    @Test
    public void checkHasItemy() {
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .statusCode(200)
                .body("places.'place name'", hasItem("Dörtağaç Köyü")) // butun place name lerin arasinda birinde var mi
        ;
    }

    @Test
    public void checkStateInResponseBody() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California"));
    }

    @Test
    public void bodyArrayHasSizeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                // .log().body()
                .statusCode(200)
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void combiningTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                // .log().body()
                .statusCode(200)
                .body("places", hasSize(1))
                .body("places.state", hasItem("California")) // verilen path deki list bu item e sahip mi
                .body("places[0].'place name'", equalTo("Beverly Hills")) // verilen path daki deger buna esit mi
        ;
    }

    /*
    http://api.zippopotam.us/us/90210                    path PARAM
    https://sonuc.osym.gov.tr/Sorgu.aspx?SonucID=9617    Query PARAM
     */

    @Test
    public void pathParamTest() {

        given()
                .pathParam("ulke", "us")
                .pathParam("postakod", 90210)
                .log().uri() // request link

                .when()
                .get("http://api.zippopotam.us/{ulke}/{postakod}")

                .then()
                .statusCode(200)
        //.log().body()
        ;
    }

    @Test
    public void queryParamTest() {

        given()
                .param("page", 1) // ?page=1  seklinde linke ekleniyor
                .log().uri() // request link

                .when()
                .get("https://gorest.co.in/public/v1/users") // ?page=1

                .then()
                .statusCode(200)
                .log().body()
        ;
    }

    @Test
    public void queryParamTest2(){
        /*
        https://gorest.co.in/public/v1/users?page=3
        bu linkteki 1 den 10 a kadar sayfalari cagirdiginizda response daki donen page degerlerinin
        cagrilan page nosu ile ayni olup olmadigini kontrol edin
         */
        for (int i = 0; i <10 ; i++) {
            given()
                    .param("page", i+1)
                    .log().uri()

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .statusCode(200)
                    .body("meta.pagination.page" , equalTo(i+1))
            ;
        }

    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void setupMethod(){
        baseURI = "https://gorest.co.in/public/v1";

        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }



    @Test
    public void test1(){
        // https://gorest.co.in/public/v1/users?page=3
        given()
                .param("page",1) //
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
                ;
    }

    @Test
    public void extractingJsonPath() {
        String countryName = given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .extract().path("country")
        ;
        // country nin degeri olan United States i String countryName degiskenine atadik
        System.out.println(countryName);
        Assert.assertEquals(countryName , "United States");

    }

    @Test
    public void extractingJsonPath2() {
        String placeName = given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .extract().path("places[0].'place name'")
                ;

        Assert.assertEquals(placeName , "Beverly Hills");

    }

    @Test
    public void extractingJsonPath3() {
        // https://gorest.co.in/public/v1/users  donen degerdeki limit bilgisini yazdirin

        int limit = given()
                .spec(requestSpec)
                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
                .extract().path("meta.pagination.limit")

                ;
        System.out.println("limit value -> " + limit);


    }

    @Test
    public void extractingJsonPath4(){
        // https://gorest.co.in/public/v1/users  donen degerdeki bütün idleri yazdirin

        ArrayList<Integer> list = given()
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
                .extract().path("data.id")
                ;
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i+1 + ". id -> " + list.get(i));
        }
    }

    @Test
    public void extractingJsonPath5(){
        // https://gorest.co.in/public/v1/users  donen degerdeki bütün name leri yazdirin

        ArrayList<String> list = given()
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
                .extract().path("data.name")
                ;
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i+1 + ". name -> " + list.get(i));
        }
    }

    @Test
    public void extractingJsonPathResponsAll(){
        // donen butun response yi response ye esitliyorm
        Response response = given()
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
                .extract().response()
                ;

        List<Integer> IDs = response.path("data.id");
        List<String> names = response.path("data.name");
        int limit = response.path("meta.pagination.limit");

        System.out.println("IDs = " + IDs);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

        Assert.assertTrue(names.contains("Gouranga Panicker"));
        Assert.assertTrue(IDs.contains(1203771));
        Assert.assertEquals(limit , 10 , "test has failed");
    }


    @Test
    public void extractJsonAll_POJO(){
        Location locationObject =
                given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .extract().body().as(Location.class)
                // Location sablonuna gore donustur
                ;

        System.out.println(locationObject.getCountry());
        System.out.println(locationObject.getPlaces()); // toString i Place' de olusturduk
    }

    @Test
    public void extractJsonAll_POJO_Task() {
        Location location =
        // asagidaki endpointte Dortagac koyune ait diger bilgileri yazdiriniz
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                //.log().body()
                .statusCode(200)
                .extract().body().as(Location.class)
        ;
        for (Place p : location.getPlaces())
            if (p.getPlacename().equals("Dörtağaç Köyü")){
                System.out.println("all datas about the object -> " + p);
            }
    }




}

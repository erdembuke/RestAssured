import Model.Task1Class;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Tasks {
    /**
     * Task2
     * create a request to https://httpstat.us/203
     * expect status 203
     * expect content type TEXT
     */

    @Test
    public void task2(){
        given()

                .when()
                .get("https://httpstat.us/203")

                .then()
                .log().all()

                ;
    }

    /** Task 1

     create a request to https://jsonplaceholder.typicode.com/todos/2
     expect status 200
     Converting Into POJO*/

    @Test
    public void task1(){
        Task1Class t1 =
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .extract().body().as(Task1Class.class)
                ;

        System.out.println(t1.toString());

    }

    /** Task 3
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     * */

    @Test
    public void task3(){

        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("title" , equalTo("quis ut nam facilis et officia qui"))

                ;
    }

    /**

     Task 4
     create a request to https://jsonplaceholder.typicode.com/todos/2
     expect status 200
     expect content type JSON
     expect response completed status to be false
     extract completed field and testNG assertion
     */

    @Test
    public void task4(){
        Task1Class t1c=
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("completed" , equalTo(false))
                .extract().body().as(Task1Class.class)
                ;
        Assert.assertFalse(t1c.isCompleted());
    }
}

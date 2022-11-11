package br.suitedetestes;

import br.desafioRest.BaseTest;
import br.desafioRestRefactor.AuthTest;
import br.desafioRestRefactor.ContasTest;
import br.desafioRestRefactor.MovimentacaoTest;
import br.desafioRestRefactor.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
    ContasTest.class,
    MovimentacaoTest.class,
    SaldoTest.class,
    AuthTest.class
})

public class Suite extends BaseTest {

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "gustavo@lourenco");
        login.put("senha", "123mudar");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");
        System.out.println(TOKEN);

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }

}

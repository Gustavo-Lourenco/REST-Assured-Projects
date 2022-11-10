package br.ce.wcacquino.rest;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    public void deveAcessarSWAPI(){
        given()
            .log().all()
        .when()
            .get("https://swapi.dev/api/people/1/")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Luke Skywalker"))
        ;
    }

    @Test
    public void deveObterClima(){
        given()
            .log().all()
            .queryParam("q", "Campinas,BR")
            .queryParam("appid", "e9eebb8beab8ef7f071324cdc9be3b1f")
            .queryParam("units", "metric")
        .when()
            .get("http://api.openweathermap.org/data/2.5/weather")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Campinas"))
            .body("coord.lon", is(-47.0608f))
        ;
    }

    @Test
    public void naoDeveAcessarSemSenha(){
        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(401)
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica(){
        given()
            .log().all()
        .when()
            .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica2(){
        given()
            .log().all()
            .auth().basic("admin", "senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenger(){
        given()
            .log().all()
            .auth().preemptive().basic("admin", "senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth2")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoComToken(){

        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "gustavo@lourenco");
        login.put("senha", "123mudar");

        //Login na API + Token
        String token = given()
            .log().all()
            .body(login)
            .contentType(ContentType.JSON)
        .when()
            .post("http://barrigarest.wcaquino.me/signin")
        .then()
            .log().all()
            .statusCode(200)
            .extract().path("token")
        ;

        given()
            .log().all()
            .header("Authorization", "JWT " + token)
        .when()
            .get("http://barrigarest.wcaquino.me/contas")
        .then()
            .log().all()
            .statusCode(200)
        ;

    }

    @Test
    public void deveAcessarAplicacaoWeb(){
        //login
         String cookie =  given()
            .log().all()
            .formParam("email", "gustavo@lourenco")
            .formParam("senha", "123mudar")
            .contentType(ContentType.URLENC.withCharset("UTF-8"))
        .when()
            .post("http://seubarriga.wcaquino.me/logar")
        .then()
            .log().all()
            .statusCode(200)
            .extract().header("set-cookie")
        ;

        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        //obter conta
        String body = given()
            .log().all()
            .cookie("connect.sid", cookie)
        .when()
            .get("http://seubarriga.wcaquino.me/contas")
        .then()
            .log().all()
            .statusCode(200)
            .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
            .extract().body().asString()
        ;

        System.out.println("----------------");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));

    }

}

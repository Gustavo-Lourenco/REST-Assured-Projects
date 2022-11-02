package br.ce.wcacquino.rest;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroItem(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", containsString("Silva"))
            .body("age", greaterThan(18));

    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas(){
        Response response = request(Method.GET, "http://restapi.wcaquino.me/users/1");

        //path
        System.out.println(Optional.ofNullable(response.path("id")));
        Assert.assertEquals(Integer.valueOf(1), response.path("id"));
        Assert.assertEquals(Integer.valueOf(1), response.path("%s", "id"));

        //jsonpath
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/users/2")
        .then()
            .statusCode(200)
            .body("name", containsString("Joaquina"))
            .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("name", containsString("Ana"))
            .body("filhos", hasSize(2))
            .body("filhos[0].name", is("Zezinho"))
            .body("filhos[1].name", is("Luizinha"))
            .body("filhos.name", hasItem("Zezinho"))
            .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void deveRetornarErroUsuarioInexistente(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/users/4")
        .then()
            .statusCode(404)
            .body("error", is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaNaRaiz(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
            .body("age[1]", is(25))
            .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
            .body("salary", contains(1234.5678f, 2500, null))
        ;

    }

}

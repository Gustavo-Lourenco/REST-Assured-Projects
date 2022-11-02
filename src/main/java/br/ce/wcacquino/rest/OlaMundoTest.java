package br.ce.wcacquino.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class OlaMundoTest {

    @Test
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        //Assert.assertTrue("O status code deveria ser 201", response.statusCode() == 201);
        Assert.assertEquals(200, response.statusCode());

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured(){
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        given()
             //Pré condition
        .when()
            .get("http://restapi.wcaquino.me/ola")
        .then()
            .statusCode(200);
    }

    @Test
    public void devoConhecerOsMatcherComHamcrest() {
        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat(128, Matchers.is(128));
        Assert.assertThat(128, Matchers.isA(Integer.class));
        Assert.assertThat(128d, Matchers.isA(Double.class));
        Assert.assertThat(128d, Matchers.greaterThan(120d));

        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, Matchers.hasSize(5));
        assertThat(impares, contains(1, 3, 5, 7, 9));
        assertThat(impares, containsInAnyOrder(9, 3, 5, 1, 7));
        assertThat(impares, hasItem(1));

        assertThat("Maria", is(not("João")));
        assertThat("Maria", not("João"));
        assertThat("Maria", anyOf(is("Maria"), is("Joaquina")));
        assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));
    }

    @Test
    public void devoValidarBody(){
        given()
        .when()
            .get("http://restapi.wcaquino.me/ola")
        .then()
            .statusCode(200)
            .body(containsString("Mundo"))
            .body(is("Ola Mundo!"))
            .body(is(not(nullValue())));
    }

}

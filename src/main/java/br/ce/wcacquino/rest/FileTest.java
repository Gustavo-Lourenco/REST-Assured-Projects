package br.ce.wcacquino.rest;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class FileTest {

    @Test
    public void deveObrigarEnvioArquivo(){
        given()
                .log().all()
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .statusCode(404)
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerUploadDoArquivo(){
        given()
            .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf"))
        .when()
            .post("http://restapi.wcaquino.me/upload")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void naoDeveFazerUploadDeArquivoGrande(){
        given()
            .log().all()
            .multiPart("arquivo", new File("src/main/resources/ArquivoGrande.pdf"))
        .when()
            .post("http://restapi.wcaquino.me/upload")
        .then()
            .log().all()
            .time(lessThan(2000L))
            .statusCode(413)
        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] image = given()
            .log().all()
            .multiPart("arquivo", new File("src/main/resources/ArquivoGrande.pdf"))
        .when()
            .get("http://restapi.wcaquino.me/download")
        .then()
            //.log().all()
            .statusCode(413)
            .extract().asByteArray();

        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        System.out.println(imagem.length());
        Assert.assertThat(imagem.length(), lessThan(100000L));
    }


}

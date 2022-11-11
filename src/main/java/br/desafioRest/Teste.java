package br.desafioRest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Teste extends BaseTest {

    private String TOKEN;

    @Before
    public void login(){

        Map<String, String> login = new HashMap<>();
        login.put("email", "gustavo@lourenco");
        login.put("senha", "123mudar");

        TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");
        System.out.println(TOKEN);
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
            .get("/contas")
        .then()
            .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){
        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{\"nome\": \"contaqualquer\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201)
            ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{\"nome\": \"conta alterada\"}")
        .when()
            .put("/contas/1474277")
        .then()
            .statusCode(200)
            .body("nome", is("conta alterada"))
        ;
    }

    @Test
    public void naoDeveAdicionarContaComNomeRepetido(){
        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{\"nome\": \"conta alterada\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(400)
            .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void deveAdicionarMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        given()
            .header("Authorization", "JWT " + TOKEN)
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao(){

        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{}")
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void naoDeveAdicionarMovimentacaoFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao("20/05/2023");

        given()
            .header("Authorization", "JWT " + TOKEN)
            .body(mov)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(1))
            .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveExcluirContaComMovimentacao(){
        given()
            .header("Authorization", "JWT " + TOKEN)
        .when()
            .delete("/contas/1474277")
        .then()
            .statusCode(500)
            .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void deveCalcularSaldoConta(){
        given()
            .header("Authorization", "JWT " + TOKEN)
        .when()
            .get("/saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == 17585}.saldo", is("100.00"))
        ;
    } //1378902

    @Test
    public void deveRemoverMovimentacao(){
        given()
            .header("Authorization", "JWT " + TOKEN)
        .when()
            .delete("/transacoes/1378902")
        .then()
            .statusCode(204)
        ;
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(1474277);
        //mov.setUsuario_id();
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao("11/06/2020");
        mov.setData_pagamento("13/12/2021");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

}

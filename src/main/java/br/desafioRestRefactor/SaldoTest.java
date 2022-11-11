package br.desafioRestRefactor;

import Utils.DateUtils;
import br.desafioRest.BaseTest;
import br.desafioRest.Movimentacao;
import org.junit.Test;

import static Utils.TestUtils.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoConta(){
        Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");

        given()
        .when()
            .get("/saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
    ;
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DateUtils.getDataDifrencaDias(-1));
        mov.setData_pagamento(DateUtils.getDataDifrencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

}

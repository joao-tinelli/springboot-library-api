package io.github.joao_tinelli.libraryapi.repository;

import io.github.joao_tinelli.libraryapi.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransacoesTest {

    @Autowired
    TransacaoService transacaoService;

    @Test
    /* TUDO ou NADA
    * Commit -> confirmar alteração
    * Rollback -> desfazer alteração
    * */
    void transacaoSimples(){
        transacaoService.executar();
    }

    @Test
    void atualizar(){
        transacaoService.atualizacaoSemAtualizar();
    }
}

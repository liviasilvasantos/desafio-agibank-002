package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClienteParserTest {

    private ClienteParser parser;
    private DadosArquivo dados;

    @BeforeEach
    void setup() {
        parser = new ClienteParser();
        dados = new DadosArquivo();
    }

    @Test
    void deveRetornarTipo002(){
        assertEquals("002", parser.getTipo());
    }

    @Test
    void deveParseLinhaValida() {
        parser.parse("002ç2345675434544345çJose da SilvaçRural", dados);
        assertEquals(1, dados.getClientes().size());
        var cliente = dados.getClientes().get(0);
        assertEquals("2345675434544345", cliente.cnpj());
        assertEquals("Jose da Silva", cliente.nome());
        assertEquals("Rural", cliente.segmentoNegocio());
    }

    @Test
    void deveLancarExcecaoParaLinhaInvalida() {
        parser.parse("002ç2345675434544345çJose da Silva", dados);
        assertTrue(dados.getClientes().isEmpty());
    }
}

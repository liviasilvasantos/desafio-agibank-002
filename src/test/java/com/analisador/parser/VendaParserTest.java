package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VendarParserTest {

    private VendaParser parser;
    private DadosArquivo dados;

    @BeforeEach
    void setup() {
        parser = new VendaParser();
        dados = new DadosArquivo();
    }

    @Test
    void deveRetornarTipo003() {
        assert parser.getTipo().equals("003");
    }

    @Test
    void deveParseLinhaValida() {
        String linha = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro";
        parser.parse(linha, dados);
        assert !dados.getVendas().isEmpty();

        var venda = dados.getVendas().get(0);
        assertEquals(10, venda.id());
        assertEquals("Pedro", venda.nomeVendedor());
        assertEquals(3, venda.itens().size());
    }

    @Test
    void deveParseItensCorretamente() {
        String linha = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro";
        parser.parse(linha, dados);
        var venda = dados.getVendas().get(0);
        var item = venda.itens().get(0);
        assertEquals(1, item.id());
        assertEquals(10, item.quantidade());
        assertEquals(100.0, item.preco(), 0.01);
    }
}

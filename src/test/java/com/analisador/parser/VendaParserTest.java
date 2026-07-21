package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VendaParserTest {

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
        var item1 = venda.itens().get(0);
        assertEquals(1, item1.id());
        assertEquals(10, item1.quantidade());
        assertEquals(100.0, item1.preco(), 0.01);

        var item2 = venda.itens().get(1);
        assertEquals(2, item2.id());
        assertEquals(30, item2.quantidade());
        assertEquals(2.50, item2.preco(), 0.01);

        var item3 = venda.itens().get(2);
        assertEquals(3, item3.id());
        assertEquals(40, item3.quantidade());
        assertEquals(3.10, item3.preco(), 0.01);
    }

    @Test
    void deveCalcularTotalVenda() {
        parser.parse("003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro", dados);
        var venda = dados.getVendas().get(0);
        assertEquals(1199, venda.totalVenda(), 0.01);
    }

    @Test
    void deveIgnorarLinhaComFormatoInvalido() {
        String linhaInvalida = "003ç10ç[1-10-100,2-30-2.50]";
        parser.parse(linhaInvalida, dados);
        assertTrue(dados.getVendas().isEmpty());
    }

    @Test
    void deveParseItensComItemMalFormado() {
        String linha = "003ç10ç[1-10-100,2-30-2.50,3-40]çPedro";
        parser.parse(linha, dados);
        assertEquals(2, dados.getVendas().get(0).itens().size());
    }
}

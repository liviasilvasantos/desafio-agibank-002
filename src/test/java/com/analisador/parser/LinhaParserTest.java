package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinhaParserTest {

    private LinhaParser linhaParser;
    private DadosArquivo dados;

    @BeforeEach
    void setup() {
        linhaParser = new LinhaParser(List.of(new VendedorParser(), new ClienteParser(), new VendaParser()));
        dados = new DadosArquivo();
    }

    @Test
    void deveParseVendedor() {
        String linha = "001ç1234567891234çPedroç50000";
        linhaParser.parse(linha, dados);
        assert !dados.getVendedores().isEmpty();
    }

    @Test
    void deveParseCliente(){
        String linha = "002ç2345675434544345çJose da SilvaçRural";
        linhaParser.parse(linha, dados);
        assert !dados.getClientes().isEmpty();
    }

    @Test
    void deveParseVenda(){
        String linha = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro";
        linhaParser.parse(linha, dados);
        assert !dados.getVendas().isEmpty();
    }

    @Test
    void deveIgnorarLinhaVazia() {
        linhaParser.parse("", dados);

        assertTrue(dados.getVendedores().isEmpty());
        assertTrue(dados.getClientes().isEmpty());
        assertTrue(dados.getVendas().isEmpty());
    }

    @Test
    void deveIgnorarLinhaNula() {
        linhaParser.parse(null, dados);

        assertTrue(dados.getVendedores().isEmpty());
        assertTrue(dados.getClientes().isEmpty());
        assertTrue(dados.getVendas().isEmpty());
    }

    @Test
    void deveIgnorarTipoDesconhecido() {
        linhaParser.parse("999çdadosçaleatorios", dados);

        assertTrue(dados.getVendedores().isEmpty());
        assertTrue(dados.getClientes().isEmpty());
        assertTrue(dados.getVendas().isEmpty());
    }

    @Test
    void deveIgnorarLinhaComErroDeParser() {
        linhaParser.parse("001ç123çNomeçNAO_E_NUMERO", dados);
        assertTrue(dados.getVendedores().isEmpty());
    }
}
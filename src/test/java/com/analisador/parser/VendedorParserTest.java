package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VendedorParserTest {

    private VendedorParser parser;
    private DadosArquivo dados;

    @BeforeEach
    void setup(){
        parser = new VendedorParser();
        dados = new DadosArquivo();
    }

    @Test
    void deveRetornarTipo001() {
        assert(parser.getTipo().equals("001"));
    }

    @Test
    void deveParseLinhaValida() {
        String linha = "001ç1234567891234çPedroç50000";
        parser.parse(linha, dados);
        assert(dados.getVendedores().size() == 1);

        var vendedor = dados.getVendedores().get(0);
        assert(vendedor.cpf().equals("1234567891234"));
        assert(vendedor.nome().equals("Pedro"));
        assert(vendedor.salario() == 50000);
    }

    @Test
    void deveParseLinhaComSalarioDecimal() {
        String linha = "001ç1234567891234çPedroç50000.75";
        parser.parse(linha, dados);
        assert(dados.getVendedores().size() == 1);

        var vendedor = dados.getVendedores().get(0);
        assert(vendedor.cpf().equals("1234567891234"));
        assert(vendedor.nome().equals("Pedro"));
        assert(vendedor.salario() == 50000.75);
    }

    @Test
    void deveIgnorarLinhaComFormatoInvalido() {
        String linha = "001ç1234567891234çPedro";
        parser.parse(linha, dados);
        assert(dados.getVendedores().isEmpty());
    }

    @Test
    void deveIgnorarLinhaComCamposExcedentes() {
        String linha = "001ç1234567891234çPedroç50000çExtra";
        parser.parse(linha, dados);
        assert(dados.getVendedores().isEmpty());
    }
}

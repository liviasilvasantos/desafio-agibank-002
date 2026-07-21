package com.analisador.service;

import com.analisador.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelatorioServiceTest {

    private RelatorioService service;

    @BeforeEach
    void setup() {
        service = new RelatorioService();
    }

    @Test
    void deveContarClientesEVendedores() {
        final DadosArquivo dados = mockDadosCompletos();
        final Relatorio relatorio = service.gerar(dados);

        assertEquals(2, relatorio.quantidadeClientes());
        assertEquals(2, relatorio.quantidadeVendedores());
    }

    @Test
    void deveIdentificarVendaMaisCara() {
        final DadosArquivo dados = mockDadosCompletos();
        final Relatorio relatorio = service.gerar(dados);

        assertEquals(1, relatorio.idVendaMaisCara());
    }

    @Test
    void deveIdentificarPiorVendedor() {
        final DadosArquivo dados = mockDadosCompletos();
        final Relatorio relatorio = service.gerar(dados);

        assertEquals("Maria", relatorio.piorVendedor());
    }

    @Test
    void deveRetornarNAQuandoSemVendas(){
        final DadosArquivo dados = new DadosArquivo();
        final Relatorio relatorio = service.gerar(dados);

        assertEquals(0, relatorio.idVendaMaisCara());
        assertEquals("N/A", relatorio.piorVendedor());
    }

    @Test
    void deveGerarRelatorioComUmaUnicaVenda() {
        final DadosArquivo dados = mockDadosComUmaVenda();
        final Relatorio relatorio = service.gerar(dados);

        assertEquals(1, relatorio.idVendaMaisCara());
        assertEquals("Carlos", relatorio.piorVendedor());
    }

    @Test
    void deveFormatarRelatorioCorretamente() {
        final Relatorio relatorio = new Relatorio(2, 2, 2, "Maria");
        final String esperado = """
        Quantidade de clientes: 2
        Quantidade de vendedores: 2
        ID da Venda mais cara: 2
        Pior Vendedor (menor volume de vendas): Maria
        """;

        assertEquals(esperado, relatorio.formatar());
    }

    private DadosArquivo mockDadosComUmaVenda() {
        final DadosArquivo dados = new DadosArquivo();
        dados.getClientes().add(new com.analisador.domain.Cliente("1234567891234", "Jose da Silva", "Rural"));
        dados.getVendedores().add(new com.analisador.domain.Vendedor("1234567891234", "Carlos", 30000));

        dados.getVendas().add(new com.analisador.domain.Venda(1, List.of(
                new com.analisador.domain.ItemVenda(1, 10, 100),
                new com.analisador.domain.ItemVenda(2, 30, 2.50)
        ), "Carlos"));
        return dados;
    }

    private DadosArquivo mockDadosCompletos() {
        final DadosArquivo dados = new DadosArquivo();
        dados.getClientes().add(new Cliente("1234567891234", "Jose da Silva", "Rural"));
        dados.getClientes().add(new Cliente("2345675434544345", "Maria Joaquina", "Urbano"));

        dados.getVendedores().add(new Vendedor("1234567891234", "Pedro", 50000));
        dados.getVendedores().add(new Vendedor("2345675434544345", "Maria", 40000));

        dados.getVendas().add(new Venda(1, List.of(
                new ItemVenda(1, 10, 100),
                new ItemVenda(2, 30, 2.50),
                new ItemVenda(3, 40, 3.10)
        ), "Pedro"));
        dados.getVendas().add(new Venda(2, List.of(
                new ItemVenda(1, 5, 200),
                new ItemVenda(2, 15, 5.00)
        ), "Maria"));
        return dados;
    }
}

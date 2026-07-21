package com.analisador.integration;

import com.analisador.parser.ClienteParser;
import com.analisador.parser.LinhaParser;
import com.analisador.parser.VendaParser;
import com.analisador.parser.VendedorParser;
import com.analisador.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FluxoCompletoTest {

    private ArquivoPipeline pipeline;
    private Path diretorioSaida;

    @BeforeEach
    void setup(@TempDir Path tempDir) {
        diretorioSaida = tempDir.resolve("out");
        final LinhaParser linhaParser = new LinhaParser(List.of(
                new VendedorParser(),
                new ClienteParser(),
                new VendaParser()
        ));

        final ArquivoProcessor processor = new ArquivoProcessor(linhaParser);
        final RelatorioService relatorioService = new RelatorioService();
        final RelatorioWriter relatorioWriter = new RelatorioWriter(diretorioSaida);

        pipeline = new ArquivoPipeline(processor, relatorioService, relatorioWriter);
    }

    @Test
    void deveProcessarArquivoCompletoEGerarRelatorio(@TempDir Path tempDir) throws IOException {
        final Path arquivo = tempDir.resolve("vendas.dat");
        Files.writeString(arquivo, """
                001ç1234567891234çPedroç50000
                001ç3245678865434çPauloç40000.99
                002ç2345675434544345çJose da SilvaçRural
                002ç2345675433444345çEduardo PereiraçRural
                003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro
                003ç08ç[1-34-10,2-33-1.50,3-40-0.10]çPaulo
                """);

        pipeline.execute(arquivo);

        final Path relatorio = diretorioSaida.resolve("vendas.dat.done.dat");
        assertTrue(Files.exists(relatorio), "Relatório não foi gerado");

        final String conteudo = Files.readString(relatorio);
        assertTrue(conteudo.contains("Quantidade de clientes: 2"));
        assertTrue(conteudo.contains("Quantidade de vendedores: 2"));
        assertTrue(conteudo.contains("ID da Venda mais cara: 10"));
        assertTrue(conteudo.contains("Pior Vendedor (menor volume de vendas): Paulo"));
    }

    @Test
    void deveProcessarArquivoComLinhasInvalidasEGerarRelatorio(@TempDir Path tempDir) throws IOException {
        final Path arquivo = tempDir.resolve("vendas_invalidas.dat");
        Files.writeString(arquivo, """
                001ç1234567891234çPedroç50000
                001ç3245678865434çPauloç40000.99
                002ç2345675434544345çJose da SilvaçRural
                002ç2345675433444345çEduardo PereiraçRural
                003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro
                003ç08ç[1-34-10,2-33-1.50,3-40-0.10]çPaulo
                004çLinha inválida
                005çOutra linha inválida
                """);

        pipeline.execute(arquivo);

        final Path relatorio = diretorioSaida.resolve("vendas_invalidas.dat.done.dat");
        assertTrue(Files.exists(relatorio), "Relatório não foi gerado");

        final String conteudo = Files.readString(relatorio);
        assertTrue(conteudo.contains("Quantidade de clientes: 2"));
        assertTrue(conteudo.contains("Quantidade de vendedores: 2"));
        assertTrue(conteudo.contains("ID da Venda mais cara: 10"));
        assertTrue(conteudo.contains("Pior Vendedor (menor volume de vendas): Paulo"));
    }

    @Test
    void deveProcedssarArquivoDeTest() throws IOException {
        final Path arquivo = Path.of("src/test/resources/dados-teste.dat").toAbsolutePath();

        pipeline.execute(arquivo);

        final Path relatorio = diretorioSaida.resolve("dados-teste.dat.done.dat");
        assertTrue(Files.exists(relatorio), "Relatório não foi gerado");

        final String conteudo = Files.readString(relatorio);
        assertTrue(conteudo.contains("Quantidade de clientes: 2"));
        assertTrue(conteudo.contains("Quantidade de vendedores: 2"));
    }

    @Test
    void deveProcessarArquivoVazio(@TempDir Path tempDir) throws IOException {
        final Path arquivo = tempDir.resolve("vendas_vazio.dat");
        Files.writeString(arquivo, "");

        pipeline.execute(arquivo);

        final Path relatorio = diretorioSaida.resolve("vendas_vazio.dat.done.dat");
        assertTrue(Files.exists(relatorio), "Relatório não foi gerado");

        final String conteudo = Files.readString(relatorio);
        assertTrue(conteudo.contains("Quantidade de clientes: 0"));
        assertTrue(conteudo.contains("Quantidade de vendedores: 0"));
    }
}

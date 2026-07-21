package com.analisador.service;

import com.analisador.domain.DadosArquivo;
import com.analisador.parser.ClienteParser;
import com.analisador.parser.LinhaParser;
import com.analisador.parser.VendaParser;
import com.analisador.parser.VendedorParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArquivoProcessorTest {

    private ArquivoProcessor processor;

    @BeforeEach
    void setup() {
        final LinhaParser linhaParser = new LinhaParser(List.of(new VendedorParser(),
                new ClienteParser(),
                new VendaParser()));
        processor = new ArquivoProcessor(linhaParser);
    }

    @Test
    void deveProcessarArquivoComTodosOsTipos(@TempDir Path tempDir) throws Exception {
         final Path arquivo = tempDir.resolve("teste.dat");
        Files.writeString(arquivo, """
        001ç1234567891234çPedroç50000
        002ç2345675434544345çJose da SilvaçRural
        003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro
        """);

        final DadosArquivo dados = processor.processar(arquivo);

        assertEquals(1, dados.getVendedores().size());
        assertEquals(1, dados.getClientes().size());
        assertEquals(1, dados.getVendas().size());
    }

    @Test
    void deveIgnorarLinhasMalFormadas(@TempDir Path tempDir) throws Exception {
        final Path arquivo = tempDir.resolve("teste_mal_formado.dat");
        Files.writeString(arquivo, """
        001ç1234567891234çPedroç50000
        LINHA_INVALIDA
        003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro
        """);

        final DadosArquivo dados = processor.processar(arquivo);

        assertEquals(1, dados.getVendedores().size());
        assertEquals(0, dados.getClientes().size());
        assertEquals(1, dados.getVendas().size());
    }

    @Test
    void deveProcessarArquivoVazio(@TempDir Path tempDir) throws Exception {
        final Path arquivo = tempDir.resolve("vazio.dat");
        Files.writeString(arquivo, "");

        final DadosArquivo dados = processor.processar(arquivo);

        assertEquals(0, dados.getVendedores().size());
        assertEquals(0, dados.getClientes().size());
        assertEquals(0, dados.getVendas().size());
    }
}

package com.analisador.service;

import com.analisador.domain.Relatorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RelatorioWriterTest {

    @Test
    void deveEscreverRelatorioComNomeCorreto(@TempDir Path tempDir) throws IOException {
        final RelatorioWriter relatorioWriter = new RelatorioWriter(tempDir);
        final Relatorio relatorio = new Relatorio(10, 5, 12345L, "João");

        relatorioWriter.execute("vendas.dat", relatorio);

        final Path arquivoSaida = tempDir.resolve("vendas.dat.done.dat");
        assert (arquivoSaida.toFile().exists());
    }

    @Test
    void deveEscreverRelatorioComConteudoCorreto(@TempDir Path tempDir) throws IOException {
        final RelatorioWriter relatorioWriter = new RelatorioWriter(tempDir);
        final Relatorio relatorio = new Relatorio(10, 5, 12345L, "João");

        relatorioWriter.execute("vendas.dat", relatorio);

        final Path arquivoSaida = tempDir.resolve("vendas.dat.done.dat");
        final String conteudoEsperado = """
        Quantidade de clientes: 10
        Quantidade de vendedores: 5
        ID da Venda mais cara: 12345
        Pior Vendedor (menor volume de vendas): João
        """;
        final String conteudoArquivo = Files.readString(arquivoSaida);
        assert (conteudoArquivo.equals(conteudoEsperado));
    }

    @Test
    void deveCriarDiretorioSaidaSeNaoExistir(@TempDir Path tempDir) throws IOException {
        final Path diretorioSaida = tempDir.resolve("saida");
        final RelatorioWriter relatorioWriter = new RelatorioWriter(diretorioSaida);
        final Relatorio relatorio = new Relatorio(10, 5, 12345L, "João");

        relatorioWriter.execute("vendas.dat", relatorio);

        assert (Files.exists(diretorioSaida));
    }
}

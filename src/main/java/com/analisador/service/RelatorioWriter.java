package com.analisador.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.analisador.domain.Relatorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelatorioWriter {

    private static final Logger log = LoggerFactory.getLogger(RelatorioWriter.class);
    private final Path diretorioSaida;

    public RelatorioWriter(final Path diretorioSaida) { this.diretorioSaida = diretorioSaida; }

    public void execute(final String nomeArquivoOriginal, final Relatorio relatorio) throws IOException {
        Files.createDirectories(diretorioSaida);

        final String nomeArquivoSaida = nomeArquivoOriginal + ".done.dat";
        final Path arquivoSaida = diretorioSaida.resolve(nomeArquivoSaida);

        Files.writeString(arquivoSaida, relatorio.formatar(), StandardCharsets.UTF_8);
        log.info("Relatório gerado: {}", arquivoSaida);
    }

}
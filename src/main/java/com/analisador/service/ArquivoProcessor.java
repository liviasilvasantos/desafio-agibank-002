package com.analisador.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.analisador.domain.DadosArquivo;
import com.analisador.parser.LinhaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArquivoProcessor {

    private static final Logger log = LoggerFactory.getLogger(ArquivoProcessor.class);
    private final LinhaParser linhaParser;

    public ArquivoProcessor(final LinhaParser linhaParser) { this.linhaParser = linhaParser; }

    public DadosArquivo processar(final Path arquivo) throws IOException {
        log.info("Processando arquivo: {}", arquivo.getFileName());

        final DadosArquivo dados = new DadosArquivo();

        try(Stream<String> linhas = Files.lines(arquivo, StandardCharsets.UTF_8)){
            linhas.forEach(linha -> linhaParser.parse(linha, dados));
        }

        log.info("Arquivo processado: {} vendedores, {} clientes, {} vendas", 
            dados.getVendedores().size(),
            dados.getClientes().size(),
            dados.getVendas().size()
        );

        return dados;
    }

}
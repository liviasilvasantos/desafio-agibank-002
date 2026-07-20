package com.analisador.service;

import java.io.IOException;
import java.nio.file.Path;

import com.analisador.domain.DadosArquivo;
import com.analisador.domain.Relatorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArquivoPipeline {

    private static final Logger log = LoggerFactory.getLogger(ArquivoPipeline.class);

    private final ArquivoProcessor processor;
    private final RelatorioService relatorioService;
    private final RelatorioWriter relatorioWriter;

    public ArquivoPipeline(final ArquivoProcessor processor, final RelatorioService relatorioService,
        final RelatorioWriter relatorioWriter){
            this.processor = processor;
            this.relatorioService = relatorioService;
            this.relatorioWriter = relatorioWriter;
    }

    public void execute(final Path arquivo){
        try {
            final DadosArquivo dados = processor.processar(arquivo);
            final Relatorio relatorio = relatorioService.gerar(dados);
            relatorioWriter.execute(arquivo.getFileName().toString(), relatorio);
        }catch(final IOException e){
            log.error("Erro ao processar arquivo {}: {}", arquivo.getFileName(), e.getMessage());
        }
    }

}
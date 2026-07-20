package com.analisador;

import java.io.IOException;
import java.util.List;

import com.analisador.config.AppConfig;
import com.analisador.parser.ClienteParser;
import com.analisador.parser.LinhaParser;
import com.analisador.parser.VendaParser;
import com.analisador.parser.VendedorParser;
import com.analisador.service.ArquivoPipeline;
import com.analisador.service.ArquivoProcessor;
import com.analisador.service.RelatorioService;
import com.analisador.service.RelatorioWriter;
import com.analisador.watcher.DiretorioWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        final AppConfig config = new AppConfig();

        final DiretorioWatcher watcher = getDiretorioWatcher(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Encerrando aplicação...");
            watcher.parar();
        }));

        try {
            log.info("Diretório de entrada: {}", config.getDiretorioEntrada());
            log.info("Diretório de saída: {}", config.getDiretorioSaida());;
            watcher.iniciar();
        }catch(final IOException e){
            log.error("Erro fatal ao iniciar a aplicação: {}", e.getMessage());
            System.exit(1);
        }
    }

    private static DiretorioWatcher getDiretorioWatcher(AppConfig config) {
        final LinhaParser linhaParser = new LinhaParser(List.of(
            new VendedorParser(),
            new ClienteParser(),
            new VendaParser()
        ));

        final ArquivoProcessor processor = new ArquivoProcessor(linhaParser);
        final RelatorioService relatorioService = new RelatorioService();
        final RelatorioWriter relatorioWriter = new RelatorioWriter(config.getDiretorioSaida());
        final ArquivoPipeline pipeline = new ArquivoPipeline(processor, relatorioService, relatorioWriter);

        return new DiretorioWatcher(config.getDiretorioEntrada(), pipeline);
    }

}
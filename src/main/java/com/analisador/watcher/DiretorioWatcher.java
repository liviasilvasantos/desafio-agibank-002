package com.analisador.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.analisador.service.ArquivoPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiretorioWatcher {

    private static final Logger log = LoggerFactory.getLogger(DiretorioWatcher.class);
    private static final String EXTENSAO_DAT = ".dat";

    @FunctionalInterface
    public interface WatchServiceSupplier {
        WatchService get() throws IOException;
    }

    private final Path diretorioEntrada;
    private final ArquivoPipeline pipeline;
    private final ExecutorService executor;
    private final WatchServiceSupplier watchServiceSupplier;
    private volatile boolean running;

    public DiretorioWatcher(final Path diretorioEntrada, final ArquivoPipeline pipeline){
        this(diretorioEntrada, pipeline, () -> FileSystems.getDefault().newWatchService(), Executors.newCachedThreadPool());
    }

    DiretorioWatcher(final Path diretorioEntrada, final ArquivoPipeline pipeline,
                     final WatchServiceSupplier watchServiceSupplier, final ExecutorService executor) {
        this.diretorioEntrada = diretorioEntrada;
        this.pipeline = pipeline;
        this.watchServiceSupplier = watchServiceSupplier;
        this.executor = executor;
    }

    public void iniciar() throws IOException {
        Files.createDirectories(diretorioEntrada);
        processarArquivosExistentes();
        monitorar();
    }

    public void parar() {
        running = false;
        executor.shutdown();
    }

    private void processarArquivosExistentes() throws IOException {
        try(Stream<Path> arquivos = Files.list(diretorioEntrada)) {
            arquivos.filter(this::isArquivoDat)
                .forEach(arquivo -> executor.submit(() -> pipeline.execute(arquivo)));
        }
    }

    protected void registrarDiretorio(final WatchService watchService) throws IOException {
        diretorioEntrada.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    private void monitorar() throws IOException {
        running = true;
        try(WatchService watchService = watchServiceSupplier.get()){
            registrarDiretorio(watchService);
            log.info("Monitorando diretorio: {}", diretorioEntrada);

            while(running) {
                WatchKey key;
                try{
                    key = watchService.take();
                } catch(final InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }

                for(WatchEvent<?> event: key.pollEvents()){
                    if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                        final Path novoArquivo = diretorioEntrada.resolve((Path) event.context());
                        if(isArquivoDat(novoArquivo)){
                            log.info("Novo arquivo detectado: {}", novoArquivo.getFileName());
                            executor.submit(() -> pipeline.execute(novoArquivo));
                        }
                    }
                }

                if(!key.reset()){
                    log.warn("Diretorio monitorado não está mais acessível");
                    break;
                }
            }
        }
    }

    private boolean isArquivoDat(final Path path) { return path.toString().endsWith(EXTENSAO_DAT); }

}
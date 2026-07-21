package com.analisador.watcher;

import com.analisador.service.ArquivoPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class DiretorioWatcherTest {

    @Mock
    private ArquivoPipeline pipeline;

    @Test
    void deveProcessarArquivosDatExistentes(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("vendas.dat"));
        Files.createFile(tempDir.resolve("clientes.dat"));
        Files.createFile(tempDir.resolve("ignorar.txt"));

        final CountDownLatch latch = new CountDownLatch(2);
        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(pipeline).execute(any());

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline);

        final Thread thread = new Thread(() -> {
            try {
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        watcher.parar();
        thread.interrupt();
        thread.join(3000);

        verify(pipeline, times(2)).execute(any());
        verify(pipeline, never()).execute(tempDir.resolve("ignorar.txt"));
    }

    @Test
    void deveDetectarNovoArquivoDat(@TempDir Path tempDir) throws Exception {
        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline);

        final CountDownLatch latch = new CountDownLatch(1);
        doAnswer(inv -> { latch.countDown(); return null;}).when(pipeline).execute(any());

        final Thread thread = new Thread(() -> {
            try {
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        Thread.sleep(500);
        Files.createFile(tempDir.resolve("novo_arquivo.dat"));
        assertTrue(Files.exists(tempDir.resolve("novo_arquivo.dat")));

        assertTrue(latch.await(15, TimeUnit.SECONDS));
        watcher.parar();
        thread.interrupt();
        thread.join(3000);

        verify(pipeline, atLeastOnce()).execute(any());
    }

    @Test
    void naoDeveProcessarArquivoNaoDat(@TempDir Path tempDir) throws Exception {
        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline);

        final Thread thread = new Thread(() -> {
            try {
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        Thread.sleep(500);
        Files.createFile(tempDir.resolve("arquivo.txt"));
        Thread.sleep(500);

        watcher.parar();
        thread.interrupt();
        thread.join(3000);

        verify(pipeline, never()).execute(any());
    }

    @Test
    void devePararCorretamente(@TempDir Path tempDir) throws Exception {
        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline);

        final Thread thread = new Thread(() -> {
            try {
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        Thread.sleep(300);
        watcher.parar();
        thread.interrupt();
        thread.join(3000);

        assertFalse(thread.isAlive());
    }

    @Test
    void deveCriarDiretorioEntradaSeNaoExistir(@TempDir Path tempDir) throws Exception {
        final Path novoDiretorio = tempDir.resolve("novo/subdir");
        assertFalse(Files.exists(novoDiretorio));

        final DiretorioWatcher watcher = new DiretorioWatcher(novoDiretorio, pipeline);

        final Thread thread = new Thread(() -> {
            try {
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        Thread.sleep(300);
        assertTrue(Files.exists(novoDiretorio));

        watcher.parar();
        thread.interrupt();
        thread.join(3000);
    }

    @Test
    void deveEncerrarQuandoDiretorioMonitoradorForRemovido(@TempDir Path tempDir) throws Exception {
        final Path diretorioMonitorado = tempDir.resolve("monitorado");
        Files.createDirectories(diretorioMonitorado);

        final DiretorioWatcher watcher = new DiretorioWatcher(diretorioMonitorado, pipeline);

        final CountDownLatch latch = new CountDownLatch(1);
        final Thread thread = new Thread(() -> {
            try {
                latch.countDown();
                watcher.iniciar();
            } catch(IOException e){

            }
        });
        thread.start();

        assertTrue(latch.await(3, TimeUnit.SECONDS));

        Thread.sleep(500);
        Files.createFile(tempDir.resolve("trigger.dat"));

        assertTrue(latch.await(15, TimeUnit.SECONDS));

        Thread.sleep(500);

        Files.walk(diretorioMonitorado)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try { Files.deleteIfExists(path); } catch(IOException e) {}
            });

        thread.join(15000);
        assertFalse(thread.isAlive());
    }
}

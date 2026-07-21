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
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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

    @Test
    void deveMonitorarQuantoEventoDiferenteDeCreate(@TempDir Path tempDir) throws Exception {
        final WatchService mockWatchService = mock(WatchService.class);
        final WatchKey mockWatchKey = mock(WatchKey.class);
        final WatchEvent<?> mockEvent = mock(WatchEvent.class);

        when(mockWatchService.take()).thenReturn(mockWatchKey);
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockEvent));
        doReturn(StandardWatchEventKinds.ENTRY_MODIFY).when(mockEvent).kind();
        when(mockWatchKey.reset()).thenReturn(false); // break the loop

        final ExecutorService mockExecutor = mock(ExecutorService.class);

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline, () -> mockWatchService, mockExecutor) {
            @Override
            protected void registrarDiretorio(WatchService ws) throws IOException {
            }
        };

        watcher.iniciar();

        verify(pipeline, never()).execute(any());
        verify(mockExecutor, never()).submit(any(Runnable.class));
    }

    @Test
    void deveMonitorarQuandoArquivoNaoDat(@TempDir Path tempDir) throws Exception {
        final WatchService mockWatchService = mock(WatchService.class);
        final WatchKey mockWatchKey = mock(WatchKey.class);
        final WatchEvent<Path> mockEvent = mock(WatchEvent.class);

        when(mockWatchService.take()).thenReturn(mockWatchKey);
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockEvent));
        doReturn(StandardWatchEventKinds.ENTRY_CREATE).when(mockEvent).kind();
        when(mockEvent.context()).thenReturn(Path.of("arquivo.txt")); // not .dat
        when(mockWatchKey.reset()).thenReturn(false); // break the loop

        final ExecutorService mockExecutor = mock(ExecutorService.class);

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline, () -> mockWatchService, mockExecutor) {
            @Override
            protected void registrarDiretorio(WatchService ws) throws IOException {
            }
        };

        watcher.iniciar();

        verify(pipeline, never()).execute(any());
        verify(mockExecutor, never()).submit(any(Runnable.class));
    }

    @Test
    void deveMonitorarQuandoArquivoDat(@TempDir Path tempDir) throws Exception {
        final WatchService mockWatchService = mock(WatchService.class);
        final WatchKey mockWatchKey = mock(WatchKey.class);
        final WatchEvent<Path> mockEvent = mock(WatchEvent.class);

        when(mockWatchService.take()).thenReturn(mockWatchKey);
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockEvent));
        doReturn(StandardWatchEventKinds.ENTRY_CREATE).when(mockEvent).kind();
        when(mockEvent.context()).thenReturn(Path.of("vendas.dat"));
        when(mockWatchKey.reset()).thenReturn(false); // break the loop

        final ExecutorService mockExecutor = mock(ExecutorService.class);
        doAnswer(inv -> {
            Runnable task = inv.getArgument(0);
            task.run();
            return null;
        }).when(mockExecutor).submit(any(Runnable.class));

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline, () -> mockWatchService, mockExecutor) {
            @Override
            protected void registrarDiretorio(WatchService ws) throws IOException {
            }
        };

        watcher.iniciar();

        verify(pipeline, times(1)).execute(tempDir.resolve("vendas.dat"));
    }

    @Test
    void deveMonitorarQuandoInterruptedException(@TempDir Path tempDir) throws Exception {
        final WatchService mockWatchService = mock(WatchService.class);
        when(mockWatchService.take()).thenThrow(new InterruptedException("Simulated interrupt"));

        final ExecutorService mockExecutor = mock(ExecutorService.class);

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline, () -> mockWatchService, mockExecutor) {
            @Override
            protected void registrarDiretorio(WatchService ws) throws IOException {
                ;
            }
        };

        Thread.interrupted();

        watcher.iniciar();

        assertTrue(Thread.currentThread().isInterrupted(), "Thread interrupt flag should be set");
        Thread.interrupted();
    }

    @Test
    void deveMonitorarQuandoPararLoop(@TempDir Path tempDir) throws Exception {
        final WatchService mockWatchService = mock(WatchService.class);
        final WatchKey mockWatchKey = mock(WatchKey.class);

        when(mockWatchService.take()).thenReturn(mockWatchKey);
        when(mockWatchKey.pollEvents()).thenReturn(List.of());

        final ExecutorService mockExecutor = mock(ExecutorService.class);

        final DiretorioWatcher watcher = new DiretorioWatcher(tempDir, pipeline, () -> mockWatchService, mockExecutor) {
            @Override
            protected void registrarDiretorio(WatchService ws) throws IOException {
            }
        };

        when(mockWatchKey.reset()).thenAnswer(inv -> {
            watcher.parar();
            return true;
        });

        watcher.iniciar();

        verify(mockWatchService, times(1)).take();
        verify(mockWatchKey, times(1)).reset();
        verify(mockExecutor, times(1)).shutdown();
    }
}

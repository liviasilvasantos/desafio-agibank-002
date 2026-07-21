package com.analisador.service;

import com.analisador.domain.DadosArquivo;
import com.analisador.domain.Relatorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArquivoPipelineTest {

    @Mock
    private ArquivoProcessor processor;

    @Mock
    private RelatorioService relatorioService;

    @Mock
    private RelatorioWriter relatorioWriter;

    @InjectMocks
    private ArquivoPipeline pipeline;

    @Test
    void deveExecutarFluxoCompleto() throws IOException {
        final Path arquivo = Path.of("teste.dat");
        final DadosArquivo dados = new DadosArquivo();
        final Relatorio relatorio = new Relatorio(1, 1, 10, "vendedor pior");

        when(processor.processar(arquivo)).thenReturn(dados);
        when(relatorioService.gerar(dados)).thenReturn(relatorio);

        pipeline.execute(arquivo);

        verify(processor).processar(arquivo);
        verify(relatorioService).gerar(dados);
        verify(relatorioWriter).execute("teste.dat", relatorio);
    }

    @Test
    void deveLogarErroQuandoProcessarFalhar() throws IOException {
        final Path arquivo = Path.of("erro.dat");
        when(processor.processar(arquivo)).thenThrow(new IOException("Erro de leitura"));

        pipeline.execute(arquivo);

        verify(processor).processar(arquivo);
        verify(relatorioService, never()).gerar(any());
        verify(relatorioWriter, never()).execute(anyString(), any());
    }
}

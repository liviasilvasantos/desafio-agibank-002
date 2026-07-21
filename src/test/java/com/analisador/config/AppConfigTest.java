package com.analisador.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AppConfigTest {

    @Test
    void deveCarregarPropriedadesDoArquivo() {
        final AppConfig config = new AppConfig();

        final String home = System.getProperty("user.home");
        assertEquals(Path.of(home, "data/in"), config.getDiretorioEntrada());
        assertEquals(Path.of(home, "data/out"), config.getDiretorioSaida());
    }

    @Test
    void deveCarregarPropriedadesDoArquivoEspecificado() {
        final AppConfig config = new AppConfig("application.properties");

        final String home = System.getProperty("user.home");
        assertEquals(Path.of(home, "data/in"), config.getDiretorioEntrada());
        assertEquals(Path.of(home, "data/out"), config.getDiretorioSaida());
    }

    @Test
    void deveUsarValoresPadraoQuandoArquivoNaoExiste() {
        final AppConfig config = new AppConfig("application_nao_existe.exe");

        final String home = System.getProperty("user.home");
        assertEquals(Path.of(home, "data/in"), config.getDiretorioEntrada());
        assertEquals(Path.of(home, "data/out"), config.getDiretorioSaida());
    }

    @Test
    void deveUsarCaminhosInformados() {
        final Path entrada = Path.of("/tmp/entrada");
        final Path saida = Path.of("/tmp/saida");
        final AppConfig config = new AppConfig(entrada, saida);

        assertEquals(entrada, config.getDiretorioEntrada());
        assertEquals(saida, config.getDiretorioSaida());
    }
}

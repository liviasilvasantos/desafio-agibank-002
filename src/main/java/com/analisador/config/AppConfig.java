package com.analisador.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class AppConfig {

    private final Path diretorioEntrada;
    private final Path diretorioSaida;

    public AppConfig() {
        final Properties props = carregaPropriedades();
        final String home = System.getProperty("user.home");

        this.diretorioEntrada = Path.of(home, props.getProperty("app.diretorio.entrada", "data/in"));
        this.diretorioSaida = Path.of(home, props.getProperty("app.diretorio.saida", "data/out"));
    }

    AppConfig(final Path diretorioEntrada, final Path diretorioSaida){
        this.diretorioEntrada = diretorioEntrada;
        this.diretorioSaida = diretorioSaida;
    }

    private Properties carregaPropriedades() {
        final Properties props = new Properties();
        try(final InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if(is != null){
                props.load(is);
            }
        } catch(final IOException e) {
            //TODO usa valores padrao
        }
        return props;
    }

    public Path getDiretorioEntrada() { return diretorioEntrada; }

    public Path getDiretorioSaida() { return diretorioSaida; }
}
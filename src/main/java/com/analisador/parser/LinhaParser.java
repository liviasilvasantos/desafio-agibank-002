package com.analisador.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.analisador.domain.DadosArquivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinhaParser {

    private static final Logger log = LoggerFactory.getLogger(LinhaParser.class);
    private final Map<String, RegistroParser> parsers = new HashMap<>();

    public LinhaParser(final List<RegistroParser> registroParsers){
        for(final RegistroParser parser: registroParsers){
            parsers.put(parser.getTipo(), parser);
        }
    }

    public void parse(final String linha, final DadosArquivo dados){
        if(linha == null || linha.isBlank()){
            return;
        }

        final String tipo = extrairTipo(linha);
        final RegistroParser parser = parsers.get(tipo);

        if(parser == null){
            log.warn("Tipo de registro desconhecido '{}' na linha {}", tipo, linha);
            return;
        }

        try{
            parser.parse(linha, dados);
        } catch(final Exception e){
            log.warn("Erro ao processar linha: {}. Motivo: {}", linha, e.getMessage());
        }
    }

    private String extrairTipo(final String linha){
        int posicao = Math.min(3, linha.length());
        return linha.substring(0, posicao).replaceAll("[^0-9]]", "");
    }
}
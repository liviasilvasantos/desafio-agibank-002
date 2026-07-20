package com.analisador.parser;

import com.analisador.domain.DadosArquivo;

public interface RegistroParser {
    String DELIMITER = "ç";

    String getTipo();
    void parse(String linha, DadosArquivo dados);
}
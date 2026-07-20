package com.analisador.parser;

import com.analisador.domain.Cliente;
import com.analisador.domain.DadosArquivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClienteParser implements RegistroParser {

    private static final Logger log = LoggerFactory.getLogger(ClienteParser.class);
    private static final String TIPO = "002";

    @Override
    public String getTipo() { return TIPO; }

    @Override
    public void parse(final String linha, final DadosArquivo dados){
        final String[] campos = linha.split(DELIMITER);
        if(campos.length != 4){
            log.warn("Linha de cliente com formato invalido: {}", linha);
            return;
        }

        final String cnpj = campos[1].trim();
        final String nome = campos[2].trim();
        final String segmento = campos[3].trim();

        dados.addCliente(new Cliente(cnpj, nome, segmento));
    }
}
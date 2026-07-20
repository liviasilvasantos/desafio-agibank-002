package com.analisador.parser;

import com.analisador.domain.DadosArquivo;
import com.analisador.domain.Vendedor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendedorParser implements RegistroParser {

    private static final Logger log = LoggerFactory.getLogger(VendedorParser.class);
    private static final String TIPO = "001";

    @Override
    public String getTipo() { return TIPO; }

    @Override
    public void parse(final String linha, final DadosArquivo dados){
        final String[] campos = linha.split(DELIMITER);
        if(campos.length != 4){
            log.warn("Linha de vendedor com formato invalido: {}", linha);
            return;
        }

        final String cpf = campos[1].trim();
        final String nome = campos[2].trim();
        final double salario = Double.parseDouble(campos[3].trim());

        dados.addVendedor(new Vendedor(cpf, nome, salario));
    }
}
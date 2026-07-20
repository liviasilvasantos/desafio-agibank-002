package com.analisador.parser;

import java.util.ArrayList;
import java.util.List;

import com.analisador.domain.DadosArquivo;
import com.analisador.domain.ItemVenda;
import com.analisador.domain.Venda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendaParser implements RegistroParser {

    private static final Logger log = LoggerFactory.getLogger(VendaParser.class);
    private static final String TIPO = "003";

    @Override
    public String getTipo() { return TIPO; }

    @Override
    public void parse(final String linha, final DadosArquivo dados){
        final String[] campos = linha.split(DELIMITER);
        if(campos.length != 4){
            log.warn("Linha de vendedor com formato invalido: {}", linha);
            return;
        }

        final long id = Long.parseLong(campos[1].trim());
        final String itensStr = campos[2].trim();
        final String nomeVendedor = campos[3].trim();

        final List<ItemVenda> itens = parseItens(itensStr);

        dados.addVenda(new Venda(id, itens, nomeVendedor));
    }

    private List<ItemVenda> parseItens(final String itensStr){
        final String conteudo = itensStr.replaceAll("[\\[\\]]", "");
        final String[] itensArray = conteudo.split(",");
        final List<ItemVenda> itens = new ArrayList<>();

        for(final String item : itensArray){
            final String[] partes = item.trim().split("-");
            if(partes.length != 3){
                log.warn("Item de venda com formato invalido: {}", item);
                continue;
            }

            long id = Long.parseLong(partes[0].trim());
            int quantidade = Integer.parseInt(partes[1].trim());
            double preco = Double.parseDouble(partes[2].trim());

            itens.add(new ItemVenda(id, quantidade, preco));
        }

        return itens;
    }
}
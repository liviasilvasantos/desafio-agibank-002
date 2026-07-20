package com.analisador.service;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import com.analisador.domain.DadosArquivo;
import com.analisador.domain.Relatorio;
import com.analisador.domain.Venda;

public class RelatorioService {


    public Relatorio gerar(final DadosArquivo dados){
        final int quantidadeClientes = dados.getClientes().size();
        final int quantidadeVendedores = dados.getVendedores().size();

        final long idVendaMaisCara = dados.getVendas().stream()
            .max(Comparator.comparingDouble(Venda::totalVenda))
            .map(Venda::id)
            .orElse(0L);

        final String piorVendedor = encontrarPiorVendedor(dados);

        return new Relatorio(quantidadeClientes, quantidadeVendedores, idVendaMaisCara, piorVendedor);
    }

    private String encontrarPiorVendedor(final DadosArquivo dados){
        if(dados.getVendas().isEmpty()){
            return "N/A";
        }

        final Map<String, Double> vendasPorVendedor = dados.getVendas().stream()
                .collect(Collectors.groupingBy(Venda::nomeVendedor,
                        Collectors.summingDouble(Venda::totalVenda)));

        return vendasPorVendedor.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}
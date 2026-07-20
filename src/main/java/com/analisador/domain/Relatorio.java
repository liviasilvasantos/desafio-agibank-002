package com.analisador.domain;

public record Relatorio(int quantidadeClientes, int quantidadeVendedores, long idVendaMaisCara, String piorVendedor){

    public String formatar() {
        return """
        Quantidade de clientes: %d
        Quantidade de vendedores: %d
        ID da Venda mais cara: %d
        Pior Vendedor (menor volume de vendas): %s
        """.formatted(quantidadeClientes, quantidadeVendedores, idVendaMaisCara, piorVendedor);
    }
}
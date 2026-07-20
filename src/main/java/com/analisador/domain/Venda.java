package com.analisador.domain;

import java.util.List;

public record Venda(long id, List<ItemVenda> itens, String nomeVendedor){

    public double totalVenda() {
        return itens.stream().mapToDouble(ItemVenda::total).sum();
    }
}
package com.analisador.domain;

public record ItemVenda(long id, int quantidade, double preco){

    public double total() { return quantidade * preco; }
}
package com.analisador.domain;

import java.util.ArrayList;
import java.util.List;

public class DadosArquivo {

    private final List<Vendedor> vendedores = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Venda> vendas = new ArrayList<>();

    public void addVendedor(final Vendedor vendedor) { vendedores.add(vendedor);}
    public void addCliente(final Cliente cliente) { clientes.add(cliente);}
    public void addVenda(final Venda venda) { vendas.add(venda);}

    public List<Vendedor> getVendedores() { return vendedores; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Venda> getVendas() { return vendas; }

}
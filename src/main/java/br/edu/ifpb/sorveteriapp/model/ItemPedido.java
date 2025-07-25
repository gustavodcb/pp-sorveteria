package br.edu.ifpb.sorveteriapp.model;

public class ItemPedido {
    private final Sorvete sorvete;
    private final int quantidade;

    public ItemPedido(Sorvete sorvete, int quantidade) {
        this.sorvete = sorvete;
        this.quantidade = quantidade;
    }

    public Sorvete getSorvete() { return sorvete; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return sorvete.getPreco(); }
    public String getDescricao() { return sorvete.getDescricao(); }
    public double getSubtotal() { return sorvete.getPreco() * quantidade; }
}

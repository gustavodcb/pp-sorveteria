package br.edu.ifpb.sorveteriapp.model;

public class Milkshake implements Sorvete {

    @Override
    public String getDescricao() {
        return "Milkshake";
    }

    @Override
    public double getPreco() {
        return 7.5;
    }

    @Override
    public String getType() {
        return "Milkshake";
    }
}


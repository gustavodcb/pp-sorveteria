package br.edu.ifpb.sorveteriapp.model;

public class SorveteMassa implements Sorvete {

    public String getDescricao() {
        return "Sorvete de Massa";
    }

    public double getPreco() {
        return 4.0;
    }

    @Override
    public String getType() {
        return "Massa";
    }
}



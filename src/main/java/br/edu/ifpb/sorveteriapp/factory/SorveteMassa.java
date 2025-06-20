package edu.ifpb.sorveteriapp.factory;

public class SorveteMassa implements Sorvete {
    @Override
    public String getType() {
        return "Sorvete de Massa";
    }

    @Override
    public Double getPrice() {
        return 4.0;
    }
}

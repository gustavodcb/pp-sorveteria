package edu.ifpb.sorveteriapp.factory;

public class Picole implements Sorvete {

    @Override
    public String getType() {
        return "Picole";
    }

    @Override
    public Double getPrice() {
        return 2.5;
    }

}

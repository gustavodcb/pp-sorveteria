package factory;

public class Milkshake implements Sorvete {

    @Override
    public String getType() {
        return "Milkshake";
    }

    @Override
    public Double getPrice() {
        return 7.5;
    }

}

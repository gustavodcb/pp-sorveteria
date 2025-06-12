package decorator;

import factory.Sorvete;

public class CaldaDecorator extends SorveteDecorator {

    public CaldaDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public String getType() {
        return sorveteModificado.getType() + ", com calda";
    }

    @Override
    public Double getPrice() {
        return sorveteModificado.getPrice() + 1.50;
    }
}

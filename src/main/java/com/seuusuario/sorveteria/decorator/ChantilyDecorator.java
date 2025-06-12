package decorator;

import factory.Sorvete;

public class ChantilyDecorator extends SorveteDecorator {

    public ChantilyDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public Double getPrice() {
        return sorveteModificado.getPrice() + 1.50;
    }

    @Override
    public String getType () {
        return sorveteModificado.getType() + ", com Chantily";
    }
}

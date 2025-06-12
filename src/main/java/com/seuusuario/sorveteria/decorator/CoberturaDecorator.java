package decorator;

import factory.Sorvete;

public class CoberturaDecorator extends SorveteDecorator {

    public CoberturaDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public String getType() {
        return sorveteModificado.getType() + ", com Cobertura";
    }

    @Override
    public Double getPrice() {
        return sorveteModificado.getPrice() + 1.50;
    }

}

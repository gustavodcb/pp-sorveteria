package edu.ifpb.sorveteriapp.decorator;

import edu.ifpb.sorveteriapp.factory.Sorvete;

public abstract class SorveteDecorator implements Sorvete {

    protected Sorvete sorveteModificado;

    public SorveteDecorator(Sorvete sorveteModificado) {
        this.sorveteModificado = sorveteModificado;
    }

    @Override
    public String getType() {
        return sorveteModificado.getType();
    }

    @Override
    public Double getPrice() {
        return sorveteModificado.getPrice();
    }


}

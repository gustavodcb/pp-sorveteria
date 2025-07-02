package br.edu.ifpb.sorveteriapp.decorator;

import br.edu.ifpb.sorveteriapp.model.Sorvete;

public abstract class SorveteDecorator implements Sorvete {

    protected Sorvete sorveteDecorado;

    public SorveteDecorator(Sorvete sorveteASerDecorado) {
        this.sorveteDecorado = sorveteASerDecorado;
    }

    @Override
    public String getDescricao() {
        return sorveteDecorado.getDescricao();
    }

    @Override
    public double getPreco() {
        return sorveteDecorado.getPreco();
    }

    @Override
    public String getType() {
        return sorveteDecorado.getType();
    }
}



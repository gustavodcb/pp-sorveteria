package br.edu.ifpb.sorveteriapp.decorator;

import br.edu.ifpb.sorveteriapp.model.Sorvete;

public class CaldaDecorator extends SorveteDecorator {

    public CaldaDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + ", com calda";
    }

    @Override
    public double getPreco() {
        return super.getPreco() + 1.50;
    }
}



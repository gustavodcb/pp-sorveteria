package br.edu.ifpb.sorveteriapp.decorator;

import br.edu.ifpb.sorveteriapp.model.Sorvete;

public class ChantilyDecorator extends SorveteDecorator {

    public ChantilyDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public double getPreco() {
        return super.getPreco() + 1.50;
    }

    @Override
    public String getDescricao () {
        return super.getDescricao() + ", com Chantily";
    }
}



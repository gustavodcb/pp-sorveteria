package br.edu.ifpb.sorveteriapp.decorator;

import br.edu.ifpb.sorveteriapp.model.Sorvete;

public class CoberturaDecorator extends SorveteDecorator {

    public CoberturaDecorator(Sorvete sorveteModificado) {
        super(sorveteModificado);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + ", com Cobertura";
    }

    @Override
    public double getPreco() {
        return super.getPreco() + 1.50;
    }

}



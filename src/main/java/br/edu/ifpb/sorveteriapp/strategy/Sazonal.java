package br.edu.ifpb.sorveteriapp.strategy;

public class Sazonal implements DescontoStrategy {

    @Override
    public double aplicarDesconto(double valor) {
        System.out.println("Aplicando disconto de 15%");
        return valor - valor * 0.15;

    }
}



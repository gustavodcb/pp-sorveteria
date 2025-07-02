package br.edu.ifpb.sorveteriapp.strategy;

public class SemDesconto implements DescontoStrategy {
    @Override
    public double aplicarDesconto(double valor) {
        System.out.println("Nenhum desconto aplicado.");
        return valor;
    }
}



package br.edu.ifpb.sorveteriapp.strategy;

public class CalculadoraDesconto {
    private DescontoStrategy descontoStrategy;

    public CalculadoraDesconto(DescontoStrategy descontoStrategy) {
        this.descontoStrategy = descontoStrategy;
    }

    public double calcularPreco(double valorOriginal) {
        if (descontoStrategy == null) {
            System.out.println("Sem desconto");
            return valorOriginal;
        }
        return descontoStrategy.aplicarDesconto(valorOriginal);
    }
}



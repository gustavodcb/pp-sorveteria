package br.edu.ifpb.sorveteriapp.strategy;

public class ClienteFrequente implements DescontoStrategy {

    @Override
    public double aplicarDesconto(double valorOriginal) {
        System.out.println("Aplicando disconto de 10% ");
        return valorOriginal - valorOriginal * 0.10;
    }
}



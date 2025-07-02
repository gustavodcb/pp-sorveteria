package br.edu.ifpb.sorveteriapp.observer;

public class MonitorDeCozinha implements PedidoObserver {
    @Override
    public void atualizar(String idPedido, String novoStatus) {
        System.out.println("-------------------------------------------");
        System.out.println("ATENÇÃO COZINHA (OBSERVER):");
        System.out.println("O pedido " + idPedido + " mudou de status para: " + novoStatus);
        System.out.println("-------------------------------------------");
    }
}


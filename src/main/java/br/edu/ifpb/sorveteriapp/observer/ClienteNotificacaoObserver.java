package br.edu.ifpb.sorveteriapp.observer;

public class ClienteNotificacaoObserver implements PedidoObserver {

    private String nomeCliente;

    public ClienteNotificacaoObserver(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    @Override
    public void atualizar(String idPedido, String status) {
        System.out.println("Cliente notificado: " + nomeCliente + "-" + idPedido);
    }
}



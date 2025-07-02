package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoEntregueState implements PedidoState {

    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Order " + pedido.getIdPedido() + " has been delivered. Enjoy!");
    }
}



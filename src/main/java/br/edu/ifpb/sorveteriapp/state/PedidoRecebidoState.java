package edu.ifpb.sorveteriapp.state;

import edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoRecebidoState implements PedidoState {

    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Order " + pedido.getIdPedido() + " received. Preparing...");
        pedido.setState(new PreparandoPedidoState());
    }
}

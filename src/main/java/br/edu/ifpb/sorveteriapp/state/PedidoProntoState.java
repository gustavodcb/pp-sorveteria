package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoProntoState implements PedidoState {
    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Pedido " + pedido.getIdPedido() + " foi ENTREGUE ao cliente.");
        pedido.setState(new PedidoEntregueState());
    }
}


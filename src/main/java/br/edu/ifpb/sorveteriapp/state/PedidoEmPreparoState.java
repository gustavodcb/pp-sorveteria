package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoEmPreparoState implements PedidoState {
    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Pedido " + pedido.getIdPedido() + " está PRONTO para entrega.");
        pedido.setEstadoPreparo(new PedidoProntoState());
    }
}


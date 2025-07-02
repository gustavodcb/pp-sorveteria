package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoRecebidoState implements PedidoState {
    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Pedido " + pedido.getIdPedido() + " enviado para a cozinha. Status: EM PREPARO.");
        pedido.setState(new PedidoEmPreparoState());
    }
}


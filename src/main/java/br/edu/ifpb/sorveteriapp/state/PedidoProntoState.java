package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoProntoState implements PedidoState {
    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Pedido " + pedido.getIdPedido().substring(0,8) + " está PRONTO e aguardando pagamento.");
        pedido.setState(new AguardandoPagamentoState());
    }
}


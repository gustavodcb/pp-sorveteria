package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class AguardandoPagamentoState implements PedidoState {

    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Aguardando pagamento para o pedido " +
                pedido.getIdPedido().substring(0,8) + ". Use a opção 'Registrar Pagamento'.");
    }

}

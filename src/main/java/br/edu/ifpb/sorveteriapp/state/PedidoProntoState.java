package br.edu.ifpb.sorveteriapp.state;

import br.edu.ifpb.sorveteriapp.model.Pedido;

public class PedidoProntoState implements PedidoState {
    @Override
    public void manusearPedido(Pedido pedido) {
        System.out.println("Ação inválida. O pedido já está pronto. O próximo passo deve ser 'Registrar Pagamento'.");
    }
}


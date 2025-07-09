package br.edu.ifpb.sorveteriapp.command;

import br.edu.ifpb.sorveteriapp.facade.PedidoFacade;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.state.AguardandoPagamentoState;
import br.edu.ifpb.sorveteriapp.state.PedidoEntregueState;

public class ProcessarPagamentoCommand implements Command {
    private final PedidoFacade facade;
    private final String pedidoId;

    public ProcessarPagamentoCommand(PedidoFacade facade, String pedidoId) {
        this.facade = facade;
        this.pedidoId = pedidoId;
    }

    @Override
    public void execute() {
        Pedido pedido = facade.buscarPedidoPorId(pedidoId);
        if (pedido == null) {
            System.out.println("ERRO: Pedido não encontrado para pagamento.");
            return;
        }

        if (pedido.getStateAtual() instanceof AguardandoPagamentoState) {
            System.out.println("Processando pagamento de R$" + String.format("%.2f", pedido.getPrecoTotal()) + " para o pedido " + pedidoId.substring(0,8));
            pedido.setState(new PedidoEntregueState());
            facade.atualizarPedido(pedido);
            System.out.println("Pagamento registrado e pedido entregue!");
        } else {
            System.out.println("Ação inválida: O pedido não está aguardando pagamento. Status atual: " + pedido.getStateAtual().getClass().getSimpleName());
        }
    }

    @Override
    public void undo() {
        // Para uma aplicação real, aqui você implementaria a lógica de estorno.
        System.out.println("Ação 'desfazer pagamento' não implementada.");
    }
}
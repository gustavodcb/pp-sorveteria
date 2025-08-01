package br.edu.ifpb.sorveteriapp.command;

import br.edu.ifpb.sorveteriapp.facade.PedidoFacade;
import br.edu.ifpb.sorveteriapp.model.Pedido;

/**
 * Command: Encapsula a ação de registrar o pagamento de um pedido.
 * Esta ação é independente do estado de preparo do pedido, permitindo
 * que o pagamento seja feito a qualquer momento.
 */
public class ProcessarPagamentoCommand implements Command {

    private final PedidoFacade facade;
    private final String pedidoId;

    public ProcessarPagamentoCommand(PedidoFacade facade, String pedidoId) {
        this.facade = facade;
        this.pedidoId = pedidoId;
    }

    /**
     * Executa a lógica de pagamento.
     * Busca o pedido, verifica se já não está pago, atualiza o status de pagamento
     * e salva a alteração no banco de dados.
     */
    @Override
    public void execute() {
        Pedido pedido = facade.buscarPedidoPorId(pedidoId);
        if (pedido == null) {
            System.err.println("ERRO: Pedido com ID " + pedidoId + " não encontrado para pagamento.");
            return;
        }

        // Verifica se o pedido já não foi pago para evitar duplicidade.
        if (pedido.getStatusPagamento().equals("PAGO")) {
            System.out.println("\n[AVISO] Este pedido já foi pago anteriormente.");
            return;
        }

        System.out.println("\nProcessando pagamento de R$" + String.format("%.2f", pedido.getPrecoTotal()) + " para o pedido de " + pedido.getNomeCliente() + "...");

        // Atualiza o status de pagamento do pedido.
        pedido.setStatusPagamento("PAGO");

        // Usa a facade para persistir a mudança no banco de dados.
        // A facade, por sua vez, chamará o repositório.
        facade.atualizarPedido(pedido);

        System.out.println("Pagamento registrado com sucesso!");
    }

    /**
     * Define a ação de desfazer (undo).
     * Em uma aplicação real, isso poderia implementar um estorno de pagamento.
     */
    @Override
    public void undo() {
        // A lógica de estorno seria complexa, envolvendo reverter o status de pagamento
        // e, possivelmente, notificar sistemas financeiros.
        // Para este projeto, uma mensagem é suficiente.
        System.out.println("Ação 'desfazer pagamento' (estorno) não implementada.");
    }
}
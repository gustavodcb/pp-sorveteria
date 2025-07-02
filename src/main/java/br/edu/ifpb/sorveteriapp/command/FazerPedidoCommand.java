package br.edu.ifpb.sorveteriapp.command;

import br.edu.ifpb.sorveteriapp.service.PedidoService;

public class FazerPedidoCommand implements Command {
    private PedidoService pedidoService;
    private String detalhesPedido;

    public FazerPedidoCommand(PedidoService pedidoService, String detalhesPedido) {
        this.pedidoService = pedidoService;
        this.detalhesPedido = detalhesPedido;
    }

    @Override
    public void execute() {
        pedidoService.FazerPedido(detalhesPedido);
    }

    @Override
    public void undo() {

    }
}



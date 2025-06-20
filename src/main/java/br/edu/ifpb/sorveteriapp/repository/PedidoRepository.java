package edu.ifpb.sorveteriapp.repository;

import edu.ifpb.sorveteriapp.model.Pedido;

import java.util.List;

public interface PedidoRepository {
    void salvarPedido(Pedido pedido);
    Pedido findById(String idPedido);
    List<Pedido> todosPedidos();
}

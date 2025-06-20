package edu.ifpb.sorveteriapp.repository;

import edu.ifpb.sorveteriapp.model.Pedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPedidoRepository implements PedidoRepository {
    private Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void salvarPedido(Pedido pedido) {
        this.pedidos.put(pedido.getIdPedido(), pedido);
        System.out.println("Pedido: " + pedido.getIdPedido() + " salvo no repositorio.");
    }

    @Override
    public Pedido findById(String idPedido) {
        return this.pedidos.get(idPedido);
    }

    @Override
    public List<Pedido> todosPedidos() {
        return new ArrayList<>(pedidos.values());
    }
}

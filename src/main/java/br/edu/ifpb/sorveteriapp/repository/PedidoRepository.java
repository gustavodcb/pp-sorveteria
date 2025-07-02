package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.model.Pedido;
import java.util.List;

public interface PedidoRepository {
    void salvar(Pedido pedido);
    Pedido buscarPorId(String id);
    List<Pedido> listarTodos();
}



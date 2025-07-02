package br.edu.ifpb.sorveteriapp.service;

public interface PedidoService {
    public default void FazerPedido(String detalhesPedido) {
        System.out.println("Pedido enviado com sucesso");
    }
}



package br.edu.ifpb.sorveteriapp.singleton;

import java.util.LinkedList;
import java.util.Queue;

public class FilaPedidos {

    private static FilaPedidos instancia;
    private Queue<String> pedidos;

    private FilaPedidos() {
        pedidos = new LinkedList<>();
    }

    public static synchronized FilaPedidos getInstancia() {
        if (instancia == null) {
            instancia = new FilaPedidos();
        }
        return instancia;
    }

    public void addPedido(String pedido) {
        pedidos.add(pedido);
        System.out.print("Pedido adicionado na fila: " + pedido);
    }

    public String proximoPedido() {
        if (!pedidos.isEmpty()) {
            String pedido = pedidos.poll();
            System.out.println("Processando pedido: " + pedido);
            return pedido;
        } else {
            System.out.println("Lista vazia");
            return null;
        }
    }

    public int getTamanho() {
        return pedidos.size();
    }

}



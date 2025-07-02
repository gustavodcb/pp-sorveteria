package br.edu.ifpb.sorveteriapp.service;

import java.util.LinkedList;
import java.util.Queue;

public class FilaDePedidos {
    private static FilaDePedidos instance;
    private final Queue<String> fila;

    private FilaDePedidos() {
        fila = new LinkedList<>();
    }

    public static synchronized FilaDePedidos getInstance() {
        if (instance == null) {
            instance = new FilaDePedidos();
        }
        return instance;
    }

    public void adicionarPedido(String idPedido) {
        fila.add(idPedido);
        System.out.println("Pedido " + idPedido + " adicionado à fila de processamento.");
    }

    public String processarProximo() {
        if (fila.isEmpty()) {
            System.out.println("Fila de pedidos está vazia.");
            return null;
        }
        String idPedido = fila.poll();
        System.out.println("Removendo pedido " + idPedido + " da fila para processamento.");
        return idPedido;
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }
}


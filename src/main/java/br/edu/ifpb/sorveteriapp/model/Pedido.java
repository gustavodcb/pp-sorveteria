package edu.ifpb.sorveteriapp.model;

import edu.ifpb.sorveteriapp.observer.PedidoObserver;
import edu.ifpb.sorveteriapp.observer.PedidoSubject;
import edu.ifpb.sorveteriapp.state.PedidoEntregueState;
import edu.ifpb.sorveteriapp.state.PedidoRecebidoState;
import edu.ifpb.sorveteriapp.state.PedidoState;

public class Pedido {
    private final String idPedido;
    private final String nomeCliente;
    private final double precoTotal;
    private PedidoState stateAtual;
    private final PedidoSubject pedidoSubject;


    public Pedido(String idPedido, String nomeCliente, double precoTotal) {
        this.idPedido = idPedido;
        this.nomeCliente = nomeCliente;
        this.precoTotal = precoTotal;
        this.pedidoSubject = new PedidoSubject();
        this.stateAtual = new PedidoRecebidoState();
        notifyObservers();
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setState(PedidoState StateAtual) {
        this.stateAtual = StateAtual;
        notifyObservers();
    }

    public void processarPedido() {
        stateAtual.manusearPedido(this);
    }

    public void anexarObserver(PedidoObserver observer) {
        pedidoSubject.addObserver(observer);
    }

    public void desanexarObserver(PedidoObserver observer) {
        pedidoSubject.removeObserver(observer);
    }

    public void notifyObservers() {
        pedidoSubject.notifyObservers(idPedido, stateAtual.getClass().getSimpleName());
    }
}

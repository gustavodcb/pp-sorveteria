package edu.ifpb.sorveteriapp.model;

import edu.ifpb.sorveteriapp.observer.PedidoObserver;
import edu.ifpb.sorveteriapp.observer.PedidoSubject;
import edu.ifpb.sorveteriapp.state.PedidoEntregueState;
import edu.ifpb.sorveteriapp.state.PedidoRecebidoState;
import edu.ifpb.sorveteriapp.state.PedidoState;

public class Pedido {
    private String idPedido;
    private String nomeCliente;
    private double precoTotal;
    private PedidoState stateAtual;
    private PedidoSubject pedidoSubject;


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

    public void notifyObserver(String status) {
        pedidoSubject.notifyObservers(idPedido, status);
    }
}

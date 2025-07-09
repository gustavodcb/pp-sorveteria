package br.edu.ifpb.sorveteriapp.model;

import br.edu.ifpb.sorveteriapp.observer.PedidoObserver;
import br.edu.ifpb.sorveteriapp.observer.PedidoSubject;
import br.edu.ifpb.sorveteriapp.state.PedidoRecebidoState;
import br.edu.ifpb.sorveteriapp.state.PedidoState;

public class Pedido {
    private final String idPedido;
    private final String nomeCliente;
    private double precoTotal; // Agora pode ser modificado pelo desconto
    private final double precoOriginal; // Novo campo
    private String descontoAplicado; // Novo campo
    private PedidoState stateAtual;
    private final PedidoSubject pedidoSubject;

    public Pedido(String idPedido, String nomeCliente, double precoOriginal) {
        this.idPedido = idPedido;
        this.nomeCliente = nomeCliente;
        this.precoOriginal = precoOriginal;
        this.precoTotal = precoOriginal; // Inicialmente, o preço total é o original
        this.descontoAplicado = "Nenhum";
        this.pedidoSubject = new PedidoSubject();
        this.stateAtual = new PedidoRecebidoState();
        notifyObservers();
    }

    // Getters
    public String getIdPedido() { return idPedido; }
    public String getNomeCliente() { return nomeCliente; }
    public double getPrecoTotal() { return precoTotal; }
    public PedidoState getStateAtual() { return this.stateAtual; }
    public double getPrecoOriginal() { return precoOriginal; }
    public String getDescontoAplicado() { return descontoAplicado; }


    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }


    public void setDescontoAplicado(String descontoAplicado) {
        this.descontoAplicado = descontoAplicado;
    }


    public void setState(PedidoState novoState) {
        this.stateAtual = novoState;
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



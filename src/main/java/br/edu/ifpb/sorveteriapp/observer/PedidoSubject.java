package br.edu.ifpb.sorveteriapp.observer;

import java.util.ArrayList;
import java.util.List;

public class PedidoSubject {

    private List<PedidoObserver> observers = new ArrayList<PedidoObserver>();

    public void addObserver(PedidoObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PedidoObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String idPedido, String status) {
        for (PedidoObserver observer : observers) {
            observer.atualizar(idPedido, status);
        }
    }
}



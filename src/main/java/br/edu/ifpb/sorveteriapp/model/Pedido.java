package br.edu.ifpb.sorveteriapp.model;

import br.edu.ifpb.sorveteriapp.observer.PedidoObserver;
import br.edu.ifpb.sorveteriapp.observer.PedidoSubject;
import br.edu.ifpb.sorveteriapp.state.PedidoRecebidoState;
import br.edu.ifpb.sorveteriapp.state.PedidoState;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private final String idPedido;
    private final String nomeCliente;
    private final PedidoSubject pedidoSubject;

    private double precoTotal; // Agora pode ser modificado pelo desconto
    private final double precoOriginal; // Novo campo
    private String descontoAplicado; // Novo campo
    private PedidoState estadoPreparo;
    private String statusPagamento;


    private List<ItemPedido> itens;

    public Pedido(String idPedido, String nomeCliente, List<ItemPedido> itens) {
        this.idPedido = idPedido;
        this.nomeCliente = nomeCliente;
        this.itens = (itens != null) ? itens : new ArrayList<>();

        this.precoOriginal = calcularPrecoOriginal();
        this.precoTotal = this.precoOriginal; // Inicialmente, o preço total é o original
        this.descontoAplicado = "Nenhum";

        this.estadoPreparo = new PedidoRecebidoState();
        this.statusPagamento = "NAO_PAGO";
        this.pedidoSubject = new PedidoSubject();

        notifyObservers();
    }

    private double calcularPrecoOriginal() {
        if (this.itens == null) return 0.0;
        return this.itens.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
}

    // Getters
    public String getIdPedido() { return idPedido; }
    public String getNomeCliente() { return nomeCliente; }
    public double getPrecoTotal() { return precoTotal; }
    public PedidoState getEstadoPreparo() { return this.estadoPreparo; }
    public String getStatusPagamento() { return statusPagamento; }
    public double getPrecoOriginal() { return precoOriginal; }
    public String getDescontoAplicado() { return descontoAplicado; }

    public List<ItemPedido> getItens() {
        return this.itens;
    }


    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }


    public void setDescontoAplicado(String descontoAplicado) {
        this.descontoAplicado = descontoAplicado;
    }


    public void setEstadoPreparo(PedidoState novoEstado) {
        this.estadoPreparo = novoEstado;
        pedidoSubject.notifyObservers(this.idPedido, "STATUS_MUDOU");
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
        String msg = statusPagamento.equals("PAGO") ? "Pagamento confirmado!" : "Pagamento pendente.";
        pedidoSubject.notifyObservers(this.idPedido, "PAGAMENTO_MUDOU");
    }



    public void processarPedido() {
        estadoPreparo.manusearPedido(this);
    }


    public void anexarObserver(PedidoObserver observer) {
        pedidoSubject.addObserver(observer);
    }


    public void desanexarObserver(PedidoObserver observer) {
        pedidoSubject.removeObserver(observer);
    }


    public void notifyObservers() {
        pedidoSubject.notifyObservers(idPedido, estadoPreparo.getClass().getSimpleName());
    }
}



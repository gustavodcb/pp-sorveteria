package br.edu.ifpb.sorveteriapp.facade;

import br.edu.ifpb.sorveteriapp.model.*;
import br.edu.ifpb.sorveteriapp.repository.ClienteRepository;
import br.edu.ifpb.sorveteriapp.repository.FuncionarioRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcClienteRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcFuncionarioRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcPedidoRepository;
import br.edu.ifpb.sorveteriapp.repository.PedidoRepository;
import br.edu.ifpb.sorveteriapp.command.Command;
import br.edu.ifpb.sorveteriapp.command.ProcessarPagamentoCommand;
import br.edu.ifpb.sorveteriapp.factory.SorveteFactory;
import br.edu.ifpb.sorveteriapp.strategy.CalculadoraDesconto;
import br.edu.ifpb.sorveteriapp.strategy.DescontoStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Facade: Ponto de entrada centralizado para todas as operações do sistema.
 * Orquestra a interação entre repositórios, factories, services e outros componentes.
 */
public class PedidoFacade {

    private final SorveteFactory sorveteFactory;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public PedidoFacade() {
        this.sorveteFactory = new SorveteFactory();
        this.pedidoRepository = new JdbcPedidoRepository();
        this.clienteRepository = new JdbcClienteRepository();
        this.funcionarioRepository = new JdbcFuncionarioRepository();
    }

    // --- MÉTODOS DE CLIENTE E FUNCIONÁRIO (sem alterações) ---
    public Cliente criarCliente(String nome) { /* ... */ return null; }
    public List<Cliente> listarTodosClientes() { /* ... */ return null; }
    public Funcionario criarFuncionario(String nome, String cargo) { /* ... */ return null; }
    public List<Funcionario> listarTodosFuncionarios() { /* ... */ return null; }


    // --- MÉTODOS DE NEGÓCIO DE PEDIDOS (CORRIGIDOS) ---

    /**
     * Cria um novo pedido com base na lista de itens do cliente.
     * @param nomeCliente O nome do cliente.
     * @param itens A lista de itens do pedido.
     * @return O objeto Pedido criado e persistido.
     */
    public Pedido fazerPedido(String nomeCliente, List<ItemPedido> itens) {
        String idPedido = UUID.randomUUID().toString();

        // CORREÇÃO 1: Passando a lista de itens para o construtor, como ele agora espera.
        Pedido pedido = new Pedido(idPedido, nomeCliente, itens);

        // A lógica de salvar em transação já está no repositório.
        pedidoRepository.salvar(pedido);

        System.out.println("Pedido para " + nomeCliente + " criado e salvo no sistema.");
        return pedido;
    }

    /**
     * Aplica uma estratégia de desconto a um pedido existente.
     * @param pedidoId O ID do pedido a ser modificado.
     * @param strategy A estratégia de desconto a ser aplicada.
     */
    public void aplicarDesconto(String pedidoId, DescontoStrategy strategy) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido != null) {
            CalculadoraDesconto calculadora = new CalculadoraDesconto(strategy);
            double precoFinalComDesconto = calculadora.calcularPreco(pedido.getPrecoOriginal());

            pedido.setPrecoTotal(precoFinalComDesconto);
            pedido.setDescontoAplicado(strategy.getClass().getSimpleName());

            this.atualizarPedido(pedido);

            // CORREÇÃO 2: Usando a variável com o nome correto.
            System.out.println("\nDesconto '" + strategy.getClass().getSimpleName() + "' aplicado! Novo preço: R$" + String.format("%.2f", precoFinalComDesconto));

        } else {
            System.err.println("ERRO: Pedido não encontrado para aplicar desconto.");
        }
    }

    /**
     * Avança o estado de preparo de um pedido específico.
     * @param pedidoId O ID do pedido a ser processado.
     */
    public void processarPedido(String pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido != null) {
            System.out.println("\nAvançando status do pedido de: " + pedido.getNomeCliente());
            pedido.processarPedido();
            pedidoRepository.salvar(pedido);
        } else {
            System.err.println("ERRO: Pedido com ID " + pedidoId + " não encontrado para processamento.");
        }
    }

    /**
     * Executa o comando para registrar o pagamento de um pedido.
     * @param pedidoId O ID do pedido a ser pago.
     */
    public void registrarPagamento(String pedidoId) {
        Command comandoPagamento = new ProcessarPagamentoCommand(this, pedidoId);
        comandoPagamento.execute();
    }

    // --- MÉTODOS DE ACESSO E AUXILIARES ---

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.listarTodos();
    }

    public Pedido buscarPedidoPorId(String id) {
        return pedidoRepository.buscarPorId(id);
    }

    /**
     * Persiste o estado atual de um objeto Pedido no banco de dados.
     * Usado por Comandos ou outras lógicas que modificam um pedido.
     * @param pedido O objeto Pedido com seu estado atualizado.
     */
    public void atualizarPedido(Pedido pedido) {
        pedidoRepository.salvar(pedido);
    }

    public SorveteFactory getSorveteFactory() {
        return this.sorveteFactory;
    }
}
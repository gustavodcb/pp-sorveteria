package br.edu.ifpb.sorveteriapp.facade;

import br.edu.ifpb.sorveteriapp.model.*;
import br.edu.ifpb.sorveteriapp.repository.*;
import br.edu.ifpb.sorveteriapp.command.*;
import br.edu.ifpb.sorveteriapp.factory.SorveteFactory;
import br.edu.ifpb.sorveteriapp.strategy.CalculadoraDesconto;
import br.edu.ifpb.sorveteriapp.strategy.DescontoStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Facade: Ponto de entrada centralizado para todas as operações do sistema.
 * Orquestra a interação entre repositórios, factories, services e outros componentes,
 * utilizando uma camada de persistência com banco de dados (JDBC).
 */
public class PedidoFacade {

    private final SorveteFactory sorveteFactory;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    // O atributo FilaDePedidos não é mais necessário aqui, simplificando a classe.

    /**
     * Construtor da Facade.
     * Inicializa todas as dependências, utilizando as implementações JDBC.
     */
    public PedidoFacade() {
        this.sorveteFactory = new SorveteFactory();
        this.pedidoRepository = new JdbcPedidoRepository();
        this.clienteRepository = new JdbcClienteRepository();
        this.funcionarioRepository = new JdbcFuncionarioRepository();
    }

    // --- MÉTODOS DE CLIENTE E FUNCIONÁRIO (permanecem iguais) ---

    public Cliente criarCliente(String nome) {
        String id = UUID.randomUUID().toString();
        Cliente cliente = new Cliente(id, nome);
        clienteRepository.salvar(cliente);
        return cliente;
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.listarTodos();
    }

    public Funcionario criarFuncionario(String nome, String cargo) {
        String id = UUID.randomUUID().toString();
        Funcionario funcionario = new Funcionario(id, nome, cargo);
        funcionarioRepository.salvar(funcionario);
        return funcionario;
    }

    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.listarTodos();
    }

    // --- MÉTODOS DE NEGÓCIO DE PEDIDOS (otimizados) ---

    /**
     * Cria um novo pedido com base na solicitação do cliente.
     * @param nomeCliente O nome do cliente.
     * @param sorvete O objeto Sorvete já montado com decorators.
     * @return O objeto Pedido criado e persistido.
     */
    public Pedido fazerPedido(String nomeCliente, Sorvete sorvete) {
        double precoOriginal = sorvete.getPreco();
        String idPedido = UUID.randomUUID().toString();

        Pedido pedido = new Pedido(idPedido, nomeCliente, precoOriginal);

        // O Observer do MonitorDeCozinha poderia ser adicionado aqui, mas para a simulação
        // em tempo real, o loop na Main do cliente cumpre melhor esse papel.
        // pedido.anexarObserver(new MonitorDeCozinha());

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
            double precoFinal = calculadora.calcularPreco(pedido.getPrecoOriginal());

            pedido.setPrecoTotal(precoFinal);
            pedido.setDescontoAplicado(strategy.getClass().getSimpleName());

            pedidoRepository.salvar(pedido);
            System.out.println("\nDesconto '" + strategy.getClass().getSimpleName() + "' aplicado! Novo preço: R$" + String.format("%.2f", precoFinal));
        } else {
            System.err.println("ERRO: Pedido não encontrado para aplicar desconto.");
        }
    }

    public void atualizarPedido(Pedido pedido) {
        pedidoRepository.salvar(pedido);
    }

    /**
     * Avança o estado de um pedido específico.
     * Este método substitui o antigo `processarProximoPedidoDaFila`.
     * @param pedidoId O ID do pedido a ser processado.
     */
    public void processarPedido(String pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido != null) {
            System.out.println("\nAvançando status do pedido de: " + pedido.getNomeCliente());
            pedido.processarPedido(); // O State Pattern faz a transição
            pedidoRepository.salvar(pedido); // Salva o novo estado no banco
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

    // --- MÉTODOS DE ACESSO A DADOS (permanecem iguais) ---

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.listarTodos();
    }

    public Pedido buscarPedidoPorId(String id) {
        return pedidoRepository.buscarPorId(id);
    }

    // O método salvarPedido não é mais necessário publicamente, pois cada ação já salva.
    // Mas pode ser mantido se preferir. Para simplificar, vou removê-lo.
    // public void salvarPedido(Pedido pedido) { ... }

    public SorveteFactory getSorveteFactory() {
        return this.sorveteFactory;
    }
}
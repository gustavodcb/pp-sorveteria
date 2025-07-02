package br.edu.ifpb.sorveteriapp.facade;

// Imports dos modelos
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.model.Cliente;
import br.edu.ifpb.sorveteriapp.model.Funcionario;

// Imports das interfaces e implementações de repositório
import br.edu.ifpb.sorveteriapp.repository.PedidoRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcPedidoRepository;
import br.edu.ifpb.sorveteriapp.repository.ClienteRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcClienteRepository;
import br.edu.ifpb.sorveteriapp.repository.FuncionarioRepository;
import br.edu.ifpb.sorveteriapp.repository.JdbcFuncionarioRepository;

// Imports dos outros padrões
import br.edu.ifpb.sorveteriapp.factory.SorveteFactory;
import br.edu.ifpb.sorveteriapp.observer.MonitorDeCozinha;
import br.edu.ifpb.sorveteriapp.service.FilaDePedidos;
import br.edu.ifpb.sorveteriapp.strategy.DescontoStrategy;
import br.edu.ifpb.sorveteriapp.strategy.CalculadoraDesconto;

import java.util.List;
import java.util.UUID;

/**
 * Facade: Ponto de entrada centralizado para todas as operações do sistema.
 * Orquestra a interação entre repositórios, factories, services e outros componentes,
 * agora utilizando uma camada de persistência com banco de dados (JDBC).
 */
public class PedidoFacade {

    private final SorveteFactory sorveteFactory;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FilaDePedidos filaDePedidos;

    /**
     * Construtor da Facade.
     * Inicializa todas as dependências, utilizando as implementações JDBC
     * para garantir a persistência dos dados no Neon DB.
     */
    public PedidoFacade() {
        this.sorveteFactory = new SorveteFactory();

        // A MUDANÇA CRÍTICA: Trocamos as implementações em memória pelas implementações JDBC.
        this.pedidoRepository = new JdbcPedidoRepository();
        this.clienteRepository = new JdbcClienteRepository();
        this.funcionarioRepository = new JdbcFuncionarioRepository();

        // O Singleton da fila de pedidos continua o mesmo.
        this.filaDePedidos = FilaDePedidos.getInstance();
    }

    // Métodos para gerenciar Clientes
    public Cliente criarCliente(String nome) {
        String id = UUID.randomUUID().toString();
        Cliente cliente = new Cliente(id, nome);
        clienteRepository.salvar(cliente);
        System.out.println("Cliente '" + nome + "' salvo no banco de dados.");
        return cliente;
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.listarTodos();
    }

    public Cliente buscarClientePorId(String id) {
        return clienteRepository.buscarPorId(id);
    }

    // Métodos para gerenciar Funcionários
    public Funcionario criarFuncionario(String nome, String cargo) {
        String id = UUID.randomUUID().toString();
        Funcionario funcionario = new Funcionario(id, nome, cargo);
        funcionarioRepository.salvar(funcionario);
        System.out.println("Funcionário '" + nome + "' salvo no banco de dados.");
        return funcionario;
    }

    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.listarTodos();
    }

    public Funcionario buscarFuncionarioPorId(String id) {
        return funcionarioRepository.buscarPorId(id);
    }

    /**
     * Versão principal do método para fazer um pedido.
     * Recebe um objeto Sorvete já montado (potencialmente com Decorators).
     *
     * @param nomeCliente      O nome do cliente que faz o pedido.
     * @param sorvete          O objeto Sorvete, já configurado.
     * @param descontoStrategy A estratégia de desconto a ser aplicada.
     * @return O objeto Pedido criado e persistido.
     */
    public Pedido fazerPedido(String nomeCliente, Sorvete sorvete, DescontoStrategy descontoStrategy) {
        System.out.println("\n--- Realizando Novo Pedido ---");
        System.out.println("Sorvete base: " + sorvete.getDescricao());

        CalculadoraDesconto calculadora = new CalculadoraDesconto(descontoStrategy);
        double precoFinal = calculadora.calcularPreco(sorvete.getPreco());

        System.out.printf("Preço final após desconto: R$%.2f\n", precoFinal);

        String idPedido = UUID.randomUUID().toString();
        Pedido pedido = new Pedido(idPedido, nomeCliente, precoFinal);

        // Anexa um observer padrão para o monitor da cozinha
        pedido.anexarObserver(new MonitorDeCozinha());

        // Salva o pedido no banco de dados em seu estado inicial (PedidoRecebidoState)
        pedidoRepository.salvar(pedido);
        System.out.println("Pedido salvo no repositório com ID: " + idPedido.substring(0, 8));

        // Adiciona à fila em memória para processamento sequencial
        filaDePedidos.adicionarPedido(pedido.getIdPedido());

        return pedido;
    }

    /**
     * Processa o próximo pedido da fila.
     * Este método simula a cozinha pegando o próximo item para preparar.
     */
    public void processarProximoPedidoDaFila() {
        System.out.println("\n--- Cozinha: Processando Próximo Pedido da Fila ---");
        String idPedido = filaDePedidos.processarProximo();

        if (idPedido != null) {
            Pedido pedido = pedidoRepository.buscarPorId(idPedido);
            if (pedido != null) {
                System.out.println("Processando pedido: " + pedido.getIdPedido().substring(0,8) + " para " + pedido.getNomeCliente());

                // Avança o estado do pedido (State Pattern)
                pedido.processarPedido();

                // Salva a alteração de estado no banco de dados
                pedidoRepository.salvar(pedido);
            } else {
                System.out.println("ERRO: Pedido com ID " + idPedido + " estava na fila mas não foi encontrado no banco de dados.");
            }
        }
    }

    /**
     * Salva o estado atual de um pedido no repositório.
     * Útil para quando uma atualização de estado é feita fora do fluxo da fila.
     * @param pedido O pedido a ser salvo.
     */
    public void salvarPedido(Pedido pedido) {
        pedidoRepository.salvar(pedido);
    }

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.listarTodos();
    }

    public Pedido buscarPedidoPorId(String id) {
        return pedidoRepository.buscarPorId(id);
    }

    /**
     * Expõe a fábrica de sorvetes para que a interface de usuário (Main)
     * possa criar a base do sorvete antes de decorá-lo.
     * @return A instância da SorveteFactory.
     */
    public SorveteFactory getSorveteFactory() {
        return this.sorveteFactory;
    }
}
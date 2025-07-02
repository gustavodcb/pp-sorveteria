package br.edu.ifpb.sorveteriapp;

import br.edu.ifpb.sorveteriapp.facade.PedidoFacade;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.decorator.*; // Importe seus decorators
import br.edu.ifpb.sorveteriapp.strategy.*; // Importe suas strategies

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final PedidoFacade facade = new PedidoFacade();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Erro: Especifique o modo de operação.");
            System.out.println("Uso: java -jar seu-programa.jar [cliente | funcionario]");
            return;
        }

        String modo = args[0].toLowerCase();
        System.out.println("====== BEM-VINDO À SORVETERIA APP (" + modo.toUpperCase() + ") ======");

        if ("cliente".equals(modo)) {
            iniciarModoCliente();
        } else if ("funcionario".equals(modo)) {
            iniciarModoFuncionario();
        } else {
            System.out.println("Modo inválido. Use 'cliente' ou 'funcionario'.");
        }
        scanner.close();
    }

    // --- MODO CLIENTE ---
    private static void iniciarModoCliente() {
        System.out.print("\nDigite seu nome: ");
        String nomeCliente = scanner.nextLine();

        // 1. Escolher o sorvete base (Factory)
        System.out.println("\n--- Escolha o tipo de sorvete ---");
        System.out.println("1. Picolé");
        System.out.println("2. Sorvete de Massa");
        System.out.println("3. Milkshake");
        System.out.print("Opção: ");
        int tipoOpcao = lerInteiro();
        String tipoSorvete = "";
        switch(tipoOpcao) {
            case 1: tipoSorvete = "Picole"; break;
            case 2: tipoSorvete = "Massa"; break;
            case 3: tipoSorvete = "Milkshake"; break;
            default: System.out.println("Opção inválida."); return;
        }

        Sorvete meuSorvete = facade.getSorveteFactory().criarSorvete(tipoSorvete);

        // 2. Personalizar o sorvete (Decorator)
        meuSorvete = adicionarExtras(meuSorvete);

        System.out.printf("\nSeu sorvete: %s | Preço parcial: R$%.2f\n", meuSorvete.getDescricao(), meuSorvete.getPreco());

        // 3. Escolher estratégia de desconto (Strategy)
        System.out.println("\n--- Escolha seu tipo de desconto ---");
        System.out.println("1. Cliente Frequente (10%)");
        System.out.println("2. Promoção Sazonal (15%)");
        System.out.println("3. Sem desconto");
        System.out.print("Opção: ");
        int descontoOpcao = lerInteiro();
        DescontoStrategy strategy;
        switch(descontoOpcao) {
            case 1: strategy = new ClienteFrequente(); break;
            case 2: strategy = new Sazonal(); break;
            default: strategy = new SemDesconto();
        }

        // 4. Fazer o pedido (Facade)
        System.out.println("\nEnviando seu pedido...");
        facade.fazerPedido(nomeCliente, meuSorvete, strategy);

        System.out.println("\nObrigado, " + nomeCliente + "! Seu pedido foi realizado com sucesso e enviado para a cozinha.");
    }

    private static Sorvete adicionarExtras(Sorvete sorvete) {
        // Implementação simplificada do Decorator
        // Fiz os decorators abstratos em concretos para poderem ser instanciados
        // Ex: public class CaldaDecorator extends SorveteDecorator { ... }
        // Se os seus já são concretos, ignore esta nota.
        // Vou assumir que você precisa corrigir isso:
        // Crie: CaldaChocolateDecorator, ChantilyDecoratorConcreto, etc. que herdam dos seus abstratos.
        // Por simplicidade aqui, vou criar classes anônimas.

        while (true) {
            System.out.println("\n--- Adicionar extras? ---");
            System.out.println("1. Cobertura (+R$1.50)");
            System.out.println("2. Calda (+R$1.50)");
            System.out.println("3. Chantily (+R$1.50)");
            System.out.println("0. Finalizar e fazer pedido");
            System.out.print("Opção: ");
            int extraOpcao = lerInteiro();

            switch (extraOpcao) {
                case 1:
                    sorvete = new CoberturaDecorator(sorvete) {}; // Usando classe anônima
                    System.out.println("Cobertura adicionada!");
                    break;
                case 2:
                    sorvete = new CaldaDecorator(sorvete) {}; // Usando classe anônima
                    System.out.println("Calda adicionada!");
                    break;
                case 3:
                    sorvete = new ChantilyDecorator(sorvete) {}; // Usando classe anônima
                    System.out.println("Chantily adicionado!");
                    break;
                case 0:
                    return sorvete;
                default:
                    System.out.println("Opção inválida.");
            }
            System.out.printf("Descrição atual: %s | Preço atual: R$%.2f\n", sorvete.getDescricao(), sorvete.getPreco());
        }
    }

    // --- MODO FUNCIONÁRIO ---
    private static void iniciarModoFuncionario() {
        while (true) {
            System.out.println("\n--- Painel do Funcionário ---");
            System.out.println("1. Ver pedidos em andamento (Recebidos e Em Preparo)");
            System.out.println("2. Ver todos os pedidos");
            System.out.println("3. Atualizar status de um pedido");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            int opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    listarPedidosEmAndamento();
                    break;
                case 2:
                    listarTodosPedidos();
                    break;
                case 3:
                    atualizarStatusPedido();
                    break;
                case 0:
                    System.out.println("Deslogando...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void listarPedidosEmAndamento() {
        System.out.println("\n--- Pedidos Ativos ---");
        List<Pedido> pedidos = facade.listarTodosPedidos().stream()
                .filter(p -> p.getStateAtual() != null)
                .toList();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido ativo no momento.");
        } else {
            pedidos.forEach(Main::imprimirDetalhesPedido);
        }
    }

    private static void listarTodosPedidos() {
        System.out.println("\n--- Histórico de Todos os Pedidos ---");
        List<Pedido> pedidos = facade.listarTodosPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido no sistema.");
        } else {
            pedidos.forEach(Main::imprimirDetalhesPedido);
        }
    }

    private static void atualizarStatusPedido() {
        System.out.print("Digite o ID do pedido para atualizar: ");
        String idPedido = scanner.nextLine();

        Pedido pedido = facade.buscarPedidoPorId(idPedido);
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        System.out.println("Processando pedido...");
        // O padrão State cuida da transição para o próximo estado.
        // O Observer notificará (no console, como já faz).
        // E o repositório salvará o novo estado no banco de dados.
        pedido.processarPedido();
        facade.salvarPedido(pedido); // Garante que a atualização de estado seja persistida

        System.out.println("Status do pedido atualizado com sucesso!");
        imprimirDetalhesPedido(facade.buscarPedidoPorId(idPedido));
    }

    private static void imprimirDetalhesPedido(Pedido p) {
        System.out.printf("ID: %s | Cliente: %-15s | Preço: R$%-7.2f | Status: %s\n",
                p.getIdPedido().substring(0, 8),
                p.getNomeCliente(),
                p.getPrecoTotal(),
                p.getStateAtual().getClass().getSimpleName());
    }

    private static int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            return -1; // Retorna um valor que geralmente é tratado como inválido.
        }
    }
}
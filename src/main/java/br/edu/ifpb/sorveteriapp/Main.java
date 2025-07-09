package br.edu.ifpb.sorveteriapp;

import br.edu.ifpb.sorveteriapp.decorator.*;
import br.edu.ifpb.sorveteriapp.facade.PedidoFacade;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.state.*;
import br.edu.ifpb.sorveteriapp.strategy.*;

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

    // =================================================================
    // MODO CLIENTE - MODIFICADO
    // =================================================================
    private static void iniciarModoCliente() {
        System.out.print("\nDigite seu nome: ");
        String nomeCliente = scanner.nextLine();

        Sorvete meuSorvete = criarSorveteInterativo();
        if (meuSorvete == null) {
            System.out.println("Criação do sorvete cancelada.");
            return;
        }

        System.out.printf("\nSeu sorvete: %s | Preço base: R$%.2f\n", meuSorvete.getDescricao(), meuSorvete.getPreco());

        // A lógica de desconto foi REMOVIDA do cliente.
        // O método fazerPedido na facade também precisa ser ajustado para não receber a strategy.
        System.out.println("\nEnviando seu pedido...");
        Pedido meuPedido = facade.fazerPedido(nomeCliente, meuSorvete);

        if(meuPedido == null) {
            System.out.println("Houve um erro ao processar seu pedido.");
            return;
        }

        System.out.println("\nObrigado! Seu pedido foi enviado. Acompanhe o status abaixo:");
        monitorarPedido(meuPedido.getIdPedido());
    }

    // Este método foi criado para organizar a criação do sorvete
    private static Sorvete criarSorveteInterativo() {
        // 1. Escolher o sorvete base (Factory)
        System.out.println("\n--- Escolha o tipo de sorvete ---");
        System.out.println("1. Picolé");
        System.out.println("2. Sorvete de Massa");
        System.out.println("3. Milkshake");
        System.out.print("Opção: ");
        int tipoOpcao = lerInteiro();
        String tipoSorvete = "";
        switch (tipoOpcao) {
            case 1: tipoSorvete = "Picole"; break;
            case 2: tipoSorvete = "Massa"; break;
            case 3: tipoSorvete = "Milkshake"; break;
            default: System.out.println("Opção inválida."); return null;
        }

        Sorvete sorveteBase = facade.getSorveteFactory().criarSorvete(tipoSorvete);

        // 2. Personalizar o sorvete (Decorator)
        return adicionarExtras(sorveteBase);
    }

    private static Sorvete adicionarExtras(Sorvete sorvete) {
        while (true) {
            System.out.println("\n--- Adicionar extras? ---");
            System.out.println("1. Cobertura (+R$1.50)");
            System.out.println("2. Calda (+R$1.50)");
            System.out.println("3. Chantily (+R$1.50)");
            System.out.println("0. Finalizar");
            System.out.print("Opção: ");
            int extraOpcao = lerInteiro();

            // Corrigindo o uso de classes anônimas
            switch (extraOpcao) {
                case 1: sorvete = new CoberturaDecorator(sorvete); System.out.println("Cobertura adicionada!"); break;
                case 2: sorvete = new CaldaDecorator(sorvete); System.out.println("Calda adicionada!"); break;
                case 3: sorvete = new ChantilyDecorator(sorvete); System.out.println("Chantily adicionado!"); break;
                case 0: return sorvete;
                default: System.out.println("Opção inválida.");
            }
            System.out.printf("Descrição atual: %s | Preço atual: R$%.2f\n", sorvete.getDescricao(), sorvete.getPreco());
        }
    }

    /**
     * SIMULAÇÃO DO OBSERVER EM TEMPO REAL PARA O CLIENTE
     */
    private static void monitorarPedido(String pedidoId) {
        String statusAnterior = "";
        while (true) {
            Pedido pedidoAtualizado = facade.buscarPedidoPorId(pedidoId);
            if (pedidoAtualizado == null) {
                System.out.println("ERRO: Seu pedido não foi encontrado no sistema.");
                break;
            }
            String statusAtual = pedidoAtualizado.getStateAtual().getClass().getSimpleName();

            if (!statusAtual.equals(statusAnterior)) {
                System.out.println("\n-------------------------------------------");
                System.out.println(">> ATUALIZAÇÃO: Seu pedido mudou de status!");
                System.out.println(">> Novo Status: " + traduzirStatus(statusAtual));
                System.out.println("-------------------------------------------");
                statusAnterior = statusAtual;
            }

            if (pedidoAtualizado.getStateAtual() instanceof PedidoEntregueState) {
                System.out.println("\nSeu pedido foi finalizado! Obrigado e volte sempre!");
                break;
            }

            try {
                Thread.sleep(5000); // Verifica a cada 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static String traduzirStatus(String statusClassName) {
        switch (statusClassName) {
            case "PedidoRecebidoState": return "Recebido pela cozinha";
            case "PedidoEmPreparoState": return "Em preparo!";
            case "PedidoProntoState": return "Pronto para retirada";
            case "AguardandoPagamentoState": return "Aguardando Pagamento no caixa";
            case "PedidoEntregueState": return "Entregue. Bom apetite!";
            default: return statusClassName;
        }
    }

    // =================================================================
    // MODO FUNCIONÁRIO - MODIFICADO
    // =================================================================
    private static void iniciarModoFuncionario() {
        while (true) {
            System.out.println("\n--- Painel do Funcionário ---");
            System.out.println("1. Gerenciar Pedidos");
            System.out.println("2. Ver Histórico de Todos os Pedidos");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            int opcao = lerInteiro();

            switch (opcao) {
                case 1: gerenciarPedidos(); break;
                case 2: listarTodosPedidos(); break;
                case 0: System.out.println("Deslogando..."); return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static void gerenciarPedidos() {
        System.out.println("\n--- Gerenciador de Pedidos ---");
        List<Pedido> pedidosAtivos = facade.listarTodosPedidos().stream()
                .filter(p -> !(p.getStateAtual() instanceof PedidoEntregueState))
                .collect(Collectors.toList());

        if (pedidosAtivos.isEmpty()) {
            System.out.println("Nenhum pedido ativo no momento. Pressione Enter para voltar.");
            scanner.nextLine();
            return;
        }

        System.out.println("Selecione o pedido para ver as opções:");
        for (int i = 0; i < pedidosAtivos.size(); i++) {
            imprimirResumoPedido(pedidosAtivos.get(i), i + 1);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("0. Voltar ao menu anterior");

        System.out.print("\nEscolha o número do pedido: ");
        int escolha = lerInteiro();

        if (escolha == 0) return;

        if (escolha > 0 && escolha <= pedidosAtivos.size()) {
            Pedido pedidoEscolhido = pedidosAtivos.get(escolha - 1);
            exibirMenuDeAcoesDoPedido(pedidoEscolhido);
        } else {
            System.out.println("Escolha inválida.");
        }
    }

    private static void exibirMenuDeAcoesDoPedido(Pedido pedido) {
        while (true) {
            System.out.println("\n--- Ações para o Pedido de " + pedido.getNomeCliente() + " (" + pedido.getIdPedido().substring(0,8) + ") ---");
            imprimirDetalhesPedido(pedido);

            System.out.println("\n1. Avançar Status do Pedido");
            System.out.println("2. Aplicar Desconto (Strategy)");
            System.out.println("3. Registrar Pagamento (Command)");
            System.out.println("0. Voltar para a lista de pedidos");
            System.out.print("Ação: ");
            int escolha = lerInteiro();

            switch (escolha) {
                case 1: facade.processarPedido(pedido.getIdPedido()); break;
                case 2: menuAplicarDesconto(pedido); break;
                case 3: facade.registrarPagamento(pedido.getIdPedido()); break;
                case 0: return;
                default: System.out.println("Opção inválida.");
            }

            pedido = facade.buscarPedidoPorId(pedido.getIdPedido());
            if (pedido.getStateAtual() instanceof PedidoEntregueState) {
                System.out.println("Pedido finalizado. Retornando à lista.");
                return;
            }
        }
    }

    private static void menuAplicarDesconto(Pedido pedido) {
        System.out.println("\n--- Aplicar Desconto (Padrão Strategy) ---");
        System.out.println("1. Cliente Frequente (10%)");
        System.out.println("2. Promoção Sazonal (15%)");
        System.out.println("3. Sem Desconto (remover)");
        System.out.print("Escolha a estratégia: ");
        int escolha = lerInteiro();
        DescontoStrategy strategy;
        switch(escolha) {
            case 1: strategy = new ClienteFrequente(); break;
            case 2: strategy = new Sazonal(); break;
            case 3: strategy = new SemDesconto(); break;
            default: System.out.println("Opção inválida."); return;
        }
        facade.aplicarDesconto(pedido.getIdPedido(), strategy);
    }

    private static void listarTodosPedidos() {
        System.out.println("\n--- Histórico de Todos os Pedidos ---");
        List<Pedido> pedidos = facade.listarTodosPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido no sistema.");
        } else {
            for(Pedido p : pedidos) {
                imprimirDetalhesPedido(p);
            }
        }
    }

    private static void imprimirResumoPedido(Pedido p, int numero) {
        System.out.printf("%d. Cliente: %-15s | Status: %-26s | Preço Final: R$%-7.2f | ID: %s... \n",
                numero,
                p.getNomeCliente(),
                traduzirStatus(p.getStateAtual().getClass().getSimpleName()),
                p.getPrecoTotal(),
                p.getIdPedido().substring(0, 8)
        );
    }

    private static void imprimirDetalhesPedido(Pedido p) {
        System.out.printf("   Status: %-26s | Preço Original: R$%-7.2f | Desconto: %-18s | Preço Final: R$%-7.2f\n",
                traduzirStatus(p.getStateAtual().getClass().getSimpleName()),
                p.getPrecoOriginal(),
                p.getDescontoAplicado(),
                p.getPrecoTotal());
    }

    private static int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            return -1;
        }
    }
}
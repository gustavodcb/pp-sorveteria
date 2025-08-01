package br.edu.ifpb.sorveteriapp;

import br.edu.ifpb.sorveteriapp.decorator.CaldaDecorator;
import br.edu.ifpb.sorveteriapp.decorator.ChantilyDecorator;
import br.edu.ifpb.sorveteriapp.decorator.CoberturaDecorator;
import br.edu.ifpb.sorveteriapp.facade.PedidoFacade;
import br.edu.ifpb.sorveteriapp.model.ItemPedido;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.state.PedidoProntoState;
import br.edu.ifpb.sorveteriapp.strategy.ClienteFrequente;
import br.edu.ifpb.sorveteriapp.strategy.DescontoStrategy;
import br.edu.ifpb.sorveteriapp.strategy.Sazonal;
import br.edu.ifpb.sorveteriapp.strategy.SemDesconto;

import java.util.ArrayList;
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

    /**
     * Exibe o painel de status completo para o cliente.
     * @param pedido O objeto Pedido com os dados mais recentes.
     */
    private static void exibirPainelCliente(Pedido pedido) {
        limparTela();
        System.out.println("====== PAINEL DO SEU PEDIDO ======");
        System.out.println("Cliente: " + pedido.getNomeCliente());
        System.out.println("ID do Pedido: " + pedido.getIdPedido().substring(0, 8) + "...");
        System.out.println("------------------------------------");

        // Exibe os itens do pedido
        System.out.println("Itens do seu pedido:");
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            System.out.println("  (Nenhum item encontrado)");
        } else {
            for (ItemPedido item : pedido.getItens()) {
                System.out.printf("  - %dx %s\n", item.getQuantidade(), item.getDescricao());
            }
        }
        System.out.println("------------------------------------");

        // Exibe o status do preparo
        System.out.println("Status do Preparo:   " + traduzirStatus(pedido.getEstadoPreparo().getClass().getSimpleName()));

        // Exibe o status do pagamento
        String statusPagamento = pedido.getStatusPagamento().equals("PAGO") ? "Confirmado!" : "Pendente";
        System.out.println("Status do Pagamento:  " + statusPagamento);

        System.out.println("------------------------------------");

        // Exibe os detalhes de preço e desconto
        String descontoStr = pedido.getDescontoAplicado() != null && !pedido.getDescontoAplicado().equals("Nenhum") && !pedido.getDescontoAplicado().equals("SemDesconto")
                ? pedido.getDescontoAplicado()
                : "Nenhum";
        System.out.printf("Valor Original:      R$ %.2f\n", pedido.getPrecoOriginal());
        System.out.printf("Desconto Aplicado:   %s\n", descontoStr);
        System.out.printf("VALOR FINAL A PAGAR: R$ %.2f\n", pedido.getPrecoTotal());

        System.out.println("====================================");
        System.out.println("\n(Este painel atualiza a cada 3 segundos)");
    }

    // =================================================================
    // MODO CLIENTE - CORRIGIDO COM LÓGICA DE CARRINHO
    // =================================================================
    private static void iniciarModoCliente() {
        System.out.print("\nDigite seu nome: ");
        String nomeCliente = scanner.nextLine();

        List<ItemPedido> carrinho = new ArrayList<>();

        while (true) {
            limparTela();
            System.out.println("--- MONTANDO SEU PEDIDO (Cliente: " + nomeCliente + ") ---");
            exibirCarrinho(carrinho);

            System.out.println("1. Adicionar novo item ao pedido");
            System.out.println("2. Finalizar e enviar pedido");
            System.out.println("0. Cancelar pedido");
            System.out.print("Opção: ");
            int opcao = lerInteiro();

            if (opcao == 1) {
                Sorvete novoSorvete = criarSorveteInterativo();
                if (novoSorvete != null) {
                    System.out.print("Digite a quantidade para '" + novoSorvete.getDescricao() + "': ");
                    int quantidade = lerInteiro();
                    if (quantidade > 0) {
                        carrinho.add(new ItemPedido(novoSorvete, quantidade));
                        System.out.println("Item adicionado ao carrinho!");
                        pressioneEnterParaContinuar();
                    }
                }
            } else if (opcao == 2) {
                if (carrinho.isEmpty()) {
                    System.out.println("Seu carrinho está vazio. Adicione um item antes de finalizar.");
                    pressioneEnterParaContinuar();
                    continue;
                }
                // Chamada correta: passando a lista de itens para a facade
                Pedido meuPedido = facade.fazerPedido(nomeCliente, carrinho);

                System.out.println("\nObrigado! Seu pedido foi enviado. Acompanhe o status abaixo:");
                monitorarPedido(meuPedido.getIdPedido());
                return; // Encerra o modo cliente
            } else if (opcao == 0) {
                System.out.println("Pedido cancelado.");
                return; // Encerra o modo cliente
            } else {
                System.out.println("Opção inválida.");
                pressioneEnterParaContinuar();
            }
        }
    }

    private static void exibirCarrinho(List<ItemPedido> carrinho) {
        if (carrinho.isEmpty()) {
            System.out.println("Seu carrinho está vazio.");
        } else {
            System.out.println("Itens no seu carrinho:");
            double subtotal = 0;
            for (int i = 0; i < carrinho.size(); i++) {
                ItemPedido item = carrinho.get(i);
                System.out.printf("  %d. %dx %s (R$ %.2f cada)\n",
                        i + 1, item.getQuantidade(), item.getDescricao(), item.getPrecoUnitario());
                subtotal += item.getSubtotal();
            }
            System.out.printf("\nSubtotal do Pedido: R$ %.2f\n", subtotal);
        }
        System.out.println("------------------------------------");
    }

    private static Sorvete criarSorveteInterativo() {
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
        return adicionarExtras(sorveteBase);
    }

    private static Sorvete adicionarExtras(Sorvete sorvete) {
        while (true) {
            System.out.println("\n--- Adicionar extras para: " + sorvete.getDescricao() + " ---");
            System.out.println("1. Cobertura (+R$1.50)");
            System.out.println("2. Calda (+R$1.50)");
            System.out.println("3. Chantily (+R$1.50)");
            System.out.println("0. Confirmar este item");
            System.out.print("Opção: ");
            int extraOpcao = lerInteiro();

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

    private static void monitorarPedido(String pedidoId) {
        while (true) {
            Pedido pedidoAtualizado = facade.buscarPedidoPorId(pedidoId);
            if (pedidoAtualizado == null) {
                System.out.println("\nERRO: Seu pedido foi cancelado ou não pôde ser encontrado.");
                break;
            }

            exibirPainelCliente(pedidoAtualizado);

            if (isPedidoFinalizado(pedidoAtualizado)) {
                System.out.println("\n>>> SEU PEDIDO ESTÁ PRONTO E PAGO! PODE RETIRAR! <<<");
                System.out.println("Obrigado por escolher nossa sorveteria!");
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Monitoramento interrompido.");
                break;
            }
        }
    }

    // =================================================================
    // MODO FUNCIONÁRIO
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
        System.out.println("\n--- Gerenciador de Pedidos Ativos ---");
        List<Pedido> pedidosAtivos = facade.listarTodosPedidos().stream()
                .filter(p -> !isPedidoFinalizado(p))
                .collect(Collectors.toList());

        if (pedidosAtivos.isEmpty()) {
            System.out.println("Nenhum pedido ativo no momento.");
            pressioneEnterParaContinuar();
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
            limparTela();
            System.out.println("--- Ações para o Pedido de " + pedido.getNomeCliente() + " (" + pedido.getIdPedido().substring(0,8) + ") ---");
            imprimirDetalhesPedido(pedido);

            System.out.println("\nOpções disponíveis:");
            if (!(pedido.getEstadoPreparo() instanceof PedidoProntoState)) {
                System.out.println("1. Avançar Status do Preparo");
            }
            System.out.println("2. Aplicar Desconto");
            if (pedido.getStatusPagamento().equals("NAO_PAGO")) {
                System.out.println("3. Registrar Pagamento");
            }
            System.out.println("0. Voltar para a lista de pedidos");
            System.out.print("Ação: ");
            int escolha = lerInteiro();

            switch (escolha) {
                case 1:
                    if (!(pedido.getEstadoPreparo() instanceof PedidoProntoState)) {
                        facade.processarPedido(pedido.getIdPedido());
                    } else {
                        System.out.println("Opção inválida para o estado atual.");
                    }
                    break;
                case 2:
                    menuAplicarDesconto(pedido);
                    break;
                case 3:
                    if (pedido.getStatusPagamento().equals("NAO_PAGO")) {
                        facade.registrarPagamento(pedido.getIdPedido());
                    } else {
                        System.out.println("Opção inválida para o estado atual.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida.");
            }

            pedido = facade.buscarPedidoPorId(pedido.getIdPedido());
            if (pedido == null || isPedidoFinalizado(pedido)) {
                System.out.println("Pedido finalizado. Retornando à lista.");
                pressioneEnterParaContinuar();
                return;
            }
            pressioneEnterParaContinuar();
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
                System.out.printf("\nPedido de: %-15s (ID: %s...)\n", p.getNomeCliente(), p.getIdPedido().substring(0, 8));
                imprimirDetalhesPedido(p);
            }
        }
        pressioneEnterParaContinuar();
    }

    // =================================================================
    // MÉTODOS AUXILIARES (HELPERS)
    // =================================================================

    public static String traduzirStatus(String statusClassName) {
        switch (statusClassName) {
            case "PedidoRecebidoState": return "Recebido pela cozinha";
            case "PedidoEmPreparoState": return "Em preparo!";
            case "PedidoProntoState": return "Pronto para retirada";
            default: return statusClassName;
        }
    }

    private static void imprimirResumoPedido(Pedido p, int numero) {
        String statusCombinado = traduzirStatus(p.getEstadoPreparo().getClass().getSimpleName()) + " (" + p.getStatusPagamento() + ")";
        System.out.printf("%d. Cliente: %-15s | Status: %-38s | Preço Final: R$%-7.2f \n",
                numero,
                p.getNomeCliente(),
                statusCombinado,
                p.getPrecoTotal()
        );
    }

    private static void imprimirDetalhesPedido(Pedido p) {
        String statusPreparo = "Preparo: " + traduzirStatus(p.getEstadoPreparo().getClass().getSimpleName());
        String statusPagamento = "Pagamento: " + p.getStatusPagamento();
        String descontoStr = p.getDescontoAplicado() != null && !p.getDescontoAplicado().equals("SemDesconto")
                ? p.getDescontoAplicado()
                : "Nenhum";

        System.out.printf("   %-30s | %-20s\n", statusPreparo, statusPagamento);
        System.out.printf("   Preço Original: R$%-7.2f | Desconto: %-18s | Preço Final: R$%-7.2f\n",
                p.getPrecoOriginal(),
                descontoStr,
                p.getPrecoTotal());

        System.out.println("   Itens do Pedido:");
        if (p.getItens() == null || p.getItens().isEmpty()) {
            System.out.println("     (Nenhum item encontrado para este pedido)");
        } else {
            for (ItemPedido item : p.getItens()) {
                System.out.printf("     - %dx %s\n", item.getQuantidade(), item.getDescricao());
            }
        }
    }

    private static int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            return -1;
        }
    }

    private static boolean isPedidoFinalizado(Pedido p) {
        return p.getEstadoPreparo() instanceof PedidoProntoState && p.getStatusPagamento().equals("PAGO");
    }

    private static void limparTela() {
        // Simples aproximação para limpar o console
        for (int i = 0; i < 30; i++) System.out.println();
    }

    private static void pressioneEnterParaContinuar() {
        System.out.println("\nPressione Enter para continuar...");
        scanner.nextLine();
    }
}
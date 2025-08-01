package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.db.ConexaoDB;
import br.edu.ifpb.sorveteriapp.model.ItemPedido;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.state.PedidoEmPreparoState;
import br.edu.ifpb.sorveteriapp.state.PedidoProntoState;
import br.edu.ifpb.sorveteriapp.state.PedidoRecebidoState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPedidoRepository implements PedidoRepository {

    @Override
    public void salvar(Pedido pedido) {
        String sqlPedido = """
            INSERT INTO pedidos (
                id_pedido, nome_cliente, preco_original, preco_total,
                desconto_aplicado, status, status_pagamento
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id_pedido) DO UPDATE SET
                preco_total = EXCLUDED.preco_total,
                desconto_aplicado = EXCLUDED.desconto_aplicado,
                status = EXCLUDED.status,
                status_pagamento = EXCLUDED.status_pagamento
            """;

        String sqlDeleteItensAntigos = "DELETE FROM itens_pedido WHERE id_pedido_fk = ?";

        String sqlItem = """
            INSERT INTO itens_pedido (
                id_pedido_fk, descricao_item, quantidade, preco_unitario
            ) VALUES (?, ?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = ConexaoDB.getConexao();
            conn.setAutoCommit(false); // Inicia a transação

            // 1. Salva/Atualiza o cabeçalho do pedido
            try (PreparedStatement pstPedido = conn.prepareStatement(sqlPedido)) {
                pstPedido.setString(1, pedido.getIdPedido());
                pstPedido.setString(2, pedido.getNomeCliente());
                pstPedido.setDouble(3, pedido.getPrecoOriginal());
                pstPedido.setDouble(4, pedido.getPrecoTotal());
                pstPedido.setString(5, pedido.getDescontoAplicado());
                pstPedido.setString(6, pedido.getEstadoPreparo().getClass().getSimpleName());
                pstPedido.setString(7, pedido.getStatusPagamento());
                pstPedido.executeUpdate();
            }

            // 2. Apaga os itens antigos para evitar duplicatas ao atualizar
            try (PreparedStatement pstDelete = conn.prepareStatement(sqlDeleteItensAntigos)) {
                pstDelete.setString(1, pedido.getIdPedido());
                pstDelete.executeUpdate();
            }

            // 3. Insere os novos itens do pedido em lote
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                try (PreparedStatement pstItem = conn.prepareStatement(sqlItem)) {
                    for (ItemPedido item : pedido.getItens()) {
                        pstItem.setString(1, pedido.getIdPedido());
                        pstItem.setString(2, item.getDescricao());
                        pstItem.setInt(3, item.getQuantidade());
                        pstItem.setDouble(4, item.getPrecoUnitario());
                        pstItem.addBatch();
                    }
                    pstItem.executeBatch();
                }
            }

            conn.commit(); // Confirma a transação

        } catch (SQLException e) {
            System.err.println("Erro na transação. Revertendo alterações.");
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignora erro no rollback */ }
            }
            throw new RuntimeException("Falha crítica ao salvar o pedido no banco de dados.", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignora erro no setAutoCommit */ }
            }
        }
    }

    @Override
    public Pedido buscarPorId(String id) {
        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPedido(rs, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY data_criacao DESC";
        try (Connection conn = ConexaoDB.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(mapRowToPedido(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    private Pedido mapRowToPedido(ResultSet rs, Connection conn) throws SQLException {
        String pedidoId = rs.getString("id_pedido");

        List<ItemPedido> itens = buscarItensPorPedidoId(pedidoId, conn);

        Pedido pedido = new Pedido(
                pedidoId,
                rs.getString("nome_cliente"),
                itens
        );

        pedido.setPrecoTotal(rs.getDouble("preco_total"));
        pedido.setDescontoAplicado(rs.getString("desconto_aplicado"));
        pedido.setStatusPagamento(rs.getString("status_pagamento"));

        String statusPreparo = rs.getString("status");
        switch (statusPreparo) {
            case "PedidoRecebidoState": pedido.setEstadoPreparo(new PedidoRecebidoState()); break;
            case "PedidoEmPreparoState": pedido.setEstadoPreparo(new PedidoEmPreparoState()); break;
            case "PedidoProntoState": pedido.setEstadoPreparo(new PedidoProntoState()); break;
            // Nenhum outro estado de preparo necessário
        }
        return pedido;
    }

    private List<ItemPedido> buscarItensPorPedidoId(String pedidoId, Connection conn) throws SQLException {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens_pedido WHERE id_pedido_fk = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, pedidoId);
            try (ResultSet rs = pst.executeQuery()) {
                while(rs.next()) {
                    String descricao = rs.getString("descricao_item");
                    double precoUnitario = rs.getDouble("preco_unitario");
                    int quantidade = rs.getInt("quantidade");

                    // Abordagem simplificada usando classe anônima para recriar o sorvete
                    Sorvete sorveteItem = new Sorvete() {
                        @Override public String getType() { return "Carregado do DB"; }
                        @Override public String getDescricao() { return descricao; }
                        @Override public double getPreco() { return precoUnitario; }
                    };

                    itens.add(new ItemPedido(sorveteItem, quantidade));
                }
            }
        }
        return itens;
    }
}
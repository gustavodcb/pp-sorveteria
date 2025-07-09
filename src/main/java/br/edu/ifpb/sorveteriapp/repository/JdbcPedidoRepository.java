package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.db.ConexaoDB;
import br.edu.ifpb.sorveteriapp.model.Pedido;
import br.edu.ifpb.sorveteriapp.state.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPedidoRepository implements PedidoRepository {

    @Override
    public void salvar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (id_pedido, nome_cliente, preco_total, status, preco_original, desconto_aplicado) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id_pedido) DO UPDATE SET nome_cliente = EXCLUDED.nome_cliente, " +
                "preco_total = EXCLUDED.preco_total, status = EXCLUDED.status, preco_original = EXCLUDED.preco_original, desconto_aplicado = EXCLUDED.desconto_aplicado";

        try (Connection conn = ConexaoDB.getConexao(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, pedido.getIdPedido());
            pst.setString(2, pedido.getNomeCliente());
            pst.setDouble(3, pedido.getPrecoTotal());
            pst.setString(4, pedido.getStateAtual().getClass().getSimpleName());
            pst.setDouble(5, pedido.getPrecoOriginal()); // Nova coluna
            pst.setString(6, pedido.getDescontoAplicado()); // Nova coluna
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha crítica ao salvar o pedido no banco de dados.", e);
        }
    }



    @Override
    public Pedido buscarPorId(String id) {
        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return mapRowToPedido(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pedido por ID: " + e.getMessage());
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
                pedidos.add(mapRowToPedido(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os pedidos: " + e.getMessage());
            e.printStackTrace();
        }
        return pedidos;
    }

    // Método auxiliar para converter uma linha do ResultSet em um objeto Pedido
    private Pedido mapRowToPedido(ResultSet rs) throws SQLException {
        String idPedido = rs.getString("id_pedido");
        String nomeCliente = rs.getString("nome_cliente");
        double precoOriginal = rs.getDouble("preco_original");

        // Cria o pedido com o preço original
        Pedido pedido = new Pedido(idPedido, nomeCliente, precoOriginal);

        // Seta os valores que podem ter sido modificados
        pedido.setPrecoTotal(rs.getDouble("preco_total"));
        pedido.setDescontoAplicado(rs.getString("desconto_aplicado"));
        String status = rs.getString("status");

        // Recria o objeto de estado correto
        switch (status) {
            case "PedidoRecebidoState": pedido.setState(new PedidoRecebidoState()); break;
            case "PedidoEmPreparoState": pedido.setState(new PedidoEmPreparoState()); break;
            case "PedidoProntoState": pedido.setState(new PedidoProntoState()); break;
            case "AguardandoPagamentoState": pedido.setState(new AguardandoPagamentoState()); break;
            case "PedidoEntregueState": pedido.setState(new PedidoEntregueState()); break;
        }
        return pedido;
    }
}

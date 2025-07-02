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
        // Usamos "ON CONFLICT" para que o mesmo método sirva para criar e atualizar.
        String sql = "INSERT INTO pedidos (id_pedido, nome_cliente, preco_total, status) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id_pedido) DO UPDATE SET nome_cliente = EXCLUDED.nome_cliente, " +
                "preco_total = EXCLUDED.preco_total, status = EXCLUDED.status";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pedido.getIdPedido());
            pst.setString(2, pedido.getNomeCliente());
            pst.setDouble(3, pedido.getPrecoTotal());
            pst.setString(4, pedido.getStateAtual().getClass().getSimpleName()); // Salva o nome da classe de estado

            pst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao salvar pedido no banco de dados: " + e.getMessage());
            e.printStackTrace();
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
        double precoTotal = rs.getDouble("preco_total");
        String status = rs.getString("status");

        Pedido pedido = new Pedido(idPedido, nomeCliente, precoTotal);

        // Recria o objeto de estado correto com base no que foi salvo no DB
        switch (status) {
            case "PedidoRecebidoState":
                pedido.setState(new PedidoRecebidoState());
                break;
            case "PedidoEmPreparoState":
                pedido.setState(new PedidoEmPreparoState());
                break;
            case "PedidoProntoState":
                pedido.setState(new PedidoProntoState());
                break;
            case "PedidoEntregueState":
                pedido.setState(new PedidoEntregueState());
                break;
            // Adicione outros estados se houver
        }
        return pedido;
    }
}
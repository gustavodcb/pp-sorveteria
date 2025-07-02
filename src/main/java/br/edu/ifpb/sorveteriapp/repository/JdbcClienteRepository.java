package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.db.ConexaoDB;
import br.edu.ifpb.sorveteriapp.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcClienteRepository implements ClienteRepository {

    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (id, nome) VALUES (?, ?) ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, cliente.getId());
            pst.setString(2, cliente.getNome());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente buscarPorId(String id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Cliente(rs.getString("id"), rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = ConexaoDB.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getString("id"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
}
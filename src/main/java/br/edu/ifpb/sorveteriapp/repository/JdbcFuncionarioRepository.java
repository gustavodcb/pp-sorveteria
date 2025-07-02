package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.db.ConexaoDB;
import br.edu.ifpb.sorveteriapp.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcFuncionarioRepository implements FuncionarioRepository {

    @Override
    public void salvar(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (id, nome, cargo) VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, cargo = EXCLUDED.cargo";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, funcionario.getId());
            pst.setString(2, funcionario.getNome());
            pst.setString(3, funcionario.getCargo());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Funcionario buscarPorId(String id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Funcionario(rs.getString("id"), rs.getString("nome"), rs.getString("cargo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios";
        try (Connection conn = ConexaoDB.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                funcionarios.add(new Funcionario(rs.getString("id"), rs.getString("nome"), rs.getString("cargo")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcionarios;
    }
}
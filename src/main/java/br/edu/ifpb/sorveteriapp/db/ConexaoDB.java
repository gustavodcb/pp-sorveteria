package br.edu.ifpb.sorveteriapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static final String URL = "jdbc:postgresql://ep-tiny-block-acidey5m-pooler.sa-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_gbTPfp3mYl5j&sslmode=require";
    private static Connection conexao;

    private ConexaoDB() {}

    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                try {
                    // System.out.println("Tentando conectar ao banco de dados..."); // Comentado
                    conexao = DriverManager.getConnection(URL);
                    // System.out.println("Conexão com o banco de dados estabelecida com sucesso!"); // Comentado
                } catch (SQLException e) {
                    // Erros críticos ainda devem ser mostrados
                    System.err.println("### ERRO CRÍTICO AO CONECTAR AO BANCO DE DADOS ###");
                    System.err.println("Mensagem: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar status da conexão: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return conexao;
    }
}

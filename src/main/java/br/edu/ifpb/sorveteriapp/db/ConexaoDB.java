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
            // Permite reconectar se a conexão foi fechada ou está inválida
            if (conexao == null || conexao.isClosed()) {
                try {

                    System.out.println("Tentando conectar ao banco de dados...");
                    conexao = DriverManager.getConnection(URL);
                    System.out.println("Conexão com o banco de dados estabelecida com sucesso!");

                } catch (SQLException e) {
                    System.err.println("### ERRO CRÍTICO AO CONECTAR AO BANCO DE DADOS ###");
                    System.err.println("Mensagem: " + e.getMessage());
                    System.err.println("Verifique se:");
                    System.err.println("1. A URL JDBC está no formato correto (host, database, user, password).");
                    System.err.println("2. O usuário e a senha estão corretos.");
                    System.err.println("3. A dependência do PostgreSQL está no seu pom.xml.");
                    System.err.println("4. Sua máquina tem acesso à internet.");
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

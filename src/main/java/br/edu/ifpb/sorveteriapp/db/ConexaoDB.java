package br.edu.ifpb.sorveteriapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static final String url = "jdbc:postgresql://neondb_owner:npg_gbTPfp3mYl5j@ep-tiny-block-acidey5m-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    private static Connection conexao;

    private ConexaoDB() {}

    public static Connection getConexao() {
        if (conexao == null) {
            try {
                conexao = DriverManager.getConnection(url);
                System.out.println("Conectado com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return conexao;
    }

}

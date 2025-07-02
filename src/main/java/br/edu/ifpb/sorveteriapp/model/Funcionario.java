
package br.edu.ifpb.sorveteriapp.model;

public class Funcionario {
    private String id;
    private String nome;
    private String cargo;

    public Funcionario(String id, String nome, String cargo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
               "id=\'" + id + '\'' +
               ", nome=\'" + nome + '\'' +
               ", cargo=\'" + cargo + '\'' +
               '}';
    }
}


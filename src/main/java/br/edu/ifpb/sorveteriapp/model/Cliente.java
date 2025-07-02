package br.edu.ifpb.sorveteriapp.model;

public class Cliente {
    private String id;
    private String nome;

    public Cliente(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "id='" + id + '\'' +
               ", nome='" + nome + '\'' +
               '}';
    }
}


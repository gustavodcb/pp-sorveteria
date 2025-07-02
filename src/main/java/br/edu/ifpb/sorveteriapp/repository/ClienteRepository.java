package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.model.Cliente;

import java.util.List;

public interface ClienteRepository {
    void salvar(Cliente cliente);
    Cliente buscarPorId(String id);
    List<Cliente> listarTodos();
}


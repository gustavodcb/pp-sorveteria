
package br.edu.ifpb.sorveteriapp.repository;

import br.edu.ifpb.sorveteriapp.model.Funcionario;

import java.util.List;

public interface FuncionarioRepository {
    void salvar(Funcionario funcionario);
    Funcionario buscarPorId(String id);
    List<Funcionario> listarTodos();
}


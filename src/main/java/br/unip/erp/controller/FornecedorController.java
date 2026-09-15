package br.unip.erp.controller;

import br.unip.erp.dao.FornecedorDAO;
import br.unip.erp.model.Fornecedor;

import java.util.List;

/** Controller da entidade FORNECEDOR (padrao MVC). */
public class FornecedorController {

    private final FornecedorDAO dao = new FornecedorDAO();

    public int salvar(Fornecedor f) {
        validar(f);
        if (f.getCodigo() == 0) {
            return dao.inserir(f);
        }
        dao.atualizar(f);
        return f.getCodigo();
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public Fornecedor buscar(int codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public List<Fornecedor> listar() {
        return dao.listar();
    }

    private void validar(Fornecedor f) {
        if (f.getPessoa() == null || f.getPessoa().getNome() == null
                || f.getPessoa().getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do fornecedor e obrigatorio.");
        }
    }
}

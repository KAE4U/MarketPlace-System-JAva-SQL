package br.unip.erp.controller;

import br.unip.erp.dao.ProdutoDAO;
import br.unip.erp.model.Produto;

import java.util.List;

/** Controller da entidade PRODUTO (padrao MVC). */
public class ProdutoController {

    private final ProdutoDAO dao = new ProdutoDAO();

    public int salvar(Produto p) {
        validar(p);
        if (p.getCodigo() == 0) {
            return dao.inserir(p);
        }
        dao.atualizar(p);
        return p.getCodigo();
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public Produto buscar(int codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public List<Produto> listar() {
        return dao.listar();
    }

    private void validar(Produto p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }
    }
}

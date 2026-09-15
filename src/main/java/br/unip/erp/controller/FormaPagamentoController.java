package br.unip.erp.controller;

import br.unip.erp.dao.FormaPagamentoDAO;
import br.unip.erp.model.FormaPagamento;

import java.util.List;

/** Controller da entidade FORMAPAGTO (padrao MVC). */
public class FormaPagamentoController {

    private final FormaPagamentoDAO dao = new FormaPagamentoDAO();

    public int salvar(FormaPagamento f) {
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

    public FormaPagamento buscar(int codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public List<FormaPagamento> listar() {
        return dao.listar();
    }

    private void validar(FormaPagamento f) {
        if (f.getNome() == null || f.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da forma de pagamento e obrigatorio.");
        }
    }
}

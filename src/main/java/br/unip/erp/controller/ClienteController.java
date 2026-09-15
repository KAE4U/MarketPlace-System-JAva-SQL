package br.unip.erp.controller;

import br.unip.erp.dao.ClienteDAO;
import br.unip.erp.model.Cliente;

import java.util.List;

/** Controller da entidade CLIENTE (padrao MVC). */
public class ClienteController {

    private final ClienteDAO dao = new ClienteDAO();

    public int salvar(Cliente c) {
        validar(c);
        if (c.getCodigo() == 0) {
            return dao.inserir(c);
        }
        dao.atualizar(c);
        return c.getCodigo();
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public Cliente buscar(int codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public List<Cliente> listar() {
        return dao.listar();
    }

    private void validar(Cliente c) {
        if (c.getPessoa() == null || c.getPessoa().getNome() == null
                || c.getPessoa().getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }
    }
}

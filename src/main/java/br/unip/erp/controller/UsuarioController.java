package br.unip.erp.controller;

import br.unip.erp.dao.UsuarioDAO;
import br.unip.erp.model.Usuario;

import java.util.List;

/** Controller da entidade USUARIO (padrao MVC). */
public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return null;
        }
        return dao.autenticar(login.trim(), senha);
    }

    public int salvar(Usuario u) {
        validar(u);
        if (u.getCodigo() == 0) {
            return dao.inserir(u);
        }
        dao.atualizar(u);
        return u.getCodigo();
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public Usuario buscar(int codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    private void validar(Usuario u) {
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new IllegalArgumentException("O login e obrigatorio.");
        }
        if (u.getSenha() == null || u.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha e obrigatoria.");
        }
    }
}

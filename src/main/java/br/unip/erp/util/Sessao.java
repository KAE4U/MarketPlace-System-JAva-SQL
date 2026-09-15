package br.unip.erp.util;

import br.unip.erp.model.Usuario;

/** Guarda o usuario autenticado durante a execucao (sessao simples). */
public final class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() { }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static void encerrar() {
        usuarioLogado = null;
    }
}

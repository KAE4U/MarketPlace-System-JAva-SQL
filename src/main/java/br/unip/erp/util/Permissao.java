package br.unip.erp.util;

import br.unip.erp.model.Usuario;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Regras de autorizacao do sistema.
 * Apenas usuarios com perfil de administrador podem excluir dados.
 */
public final class Permissao {

    private Permissao() { }

    /** True se o usuario logado tem perfil de administrador. */
    public static boolean isAdmin() {
        Usuario u = Sessao.getUsuarioLogado();
        return u != null && u.isAdmin();
    }

    /**
     * Verifica se o usuario logado pode excluir dados. Caso nao possa,
     * exibe uma mensagem informando a falta de permissao e retorna false.
     *
     * @param parente componente para ancorar o dialogo (pode ser null)
     * @return true se pode excluir; false caso contrario
     */
    public static boolean podeExcluir(Component parente) {
        if (isAdmin()) {
            return true;
        }
        JOptionPane.showMessageDialog(parente,
                "Não é possível deletar: seu usuário não tem permissão de administrador no sistema.",
                "Permissão negada",
                JOptionPane.WARNING_MESSAGE);
        return false;
    }
}

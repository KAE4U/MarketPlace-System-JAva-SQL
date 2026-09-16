package br.unip.erp;

import br.unip.erp.util.Tema;
import br.unip.erp.view.LoginView;

import javax.swing.SwingUtilities;

/**
 * Classe principal do Sistema ERP de Compra e Venda.
 * Aplica o tema visual (FlatLaf dark) e exibe a tela de login.
 */
public class Main {

    public static void main(String[] args) {
        // Aplica o Look & Feel moderno (tema escuro) antes de criar qualquer tela
        Tema.instalar();
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}

package br.unip.erp;

import br.unip.erp.view.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principal do Sistema ERP de Compra e Venda.
 * Inicia a aplicacao exibindo a tela de login.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // usa o look and feel padrao caso o do sistema falhe
        }
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}

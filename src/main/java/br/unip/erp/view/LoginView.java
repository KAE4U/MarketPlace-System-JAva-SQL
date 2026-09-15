package br.unip.erp.view;

import br.unip.erp.controller.UsuarioController;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.Sessao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Tela de login do sistema. */
public class LoginView extends JFrame {

    private final UsuarioController controller = new UsuarioController();
    private final JTextField txtLogin = new JTextField(18);
    private final JPasswordField txtSenha = new JPasswordField(18);

    public LoginView() {
        setTitle("EMPRESA X - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        montarTela();
    }

    private void montarTela() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(30, 30, 40));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titulo = new JLabel("Faca o seu login");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        painel.add(titulo, gbc);

        gbc.gridy++;
        JLabel lblLogin = new JLabel("Login");
        lblLogin.setForeground(Color.LIGHT_GRAY);
        painel.add(lblLogin, gbc);

        gbc.gridy++;
        painel.add(txtLogin, gbc);

        gbc.gridy++;
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setForeground(Color.LIGHT_GRAY);
        painel.add(lblSenha, gbc);

        gbc.gridy++;
        painel.add(txtSenha, gbc);

        gbc.gridy++;
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(new Color(120, 80, 200));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new Dimension(0, 36));
        btnEntrar.addActionListener(e -> entrar());
        painel.add(btnEntrar, gbc);

        gbc.gridy++;
        JLabel dica = new JLabel("Usuario padrao: admin / senha: admin");
        dica.setForeground(Color.GRAY);
        dica.setFont(new Font("SansSerif", Font.ITALIC, 11));
        painel.add(dica, gbc);

        // Enter na senha dispara o login
        txtSenha.addActionListener(e -> entrar());

        setContentPane(painel);
    }

    private void entrar() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        try {
            Usuario u = controller.autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this,
                        "Login ou senha invalidos.",
                        "Acesso negado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Sessao.setUsuarioLogado(u);
            dispose();
            new MenuView().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package br.unip.erp.view;

import br.unip.erp.controller.UsuarioController;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.Sessao;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Tela de login do sistema (visual moderno, tema escuro). */
public class LoginView extends JFrame {

    private final UsuarioController controller = new UsuarioController();
    private final JTextField txtLogin = new JTextField(18);
    private final JPasswordField txtSenha = new JPasswordField(18);

    public LoginView() {
        setTitle("EMPRESA X - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        montarTela();
    }

    private void montarTela() {
        // Fundo geral
        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(Tema.FUNDO);

        // Card central
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Tema.SUPERFICIE);
        card.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        card.putClientProperty("FlatLaf.style", "arc: 18");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Titulo com "ponto" de acento
        JLabel titulo = new JLabel("Faça o seu login");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO);
        card.add(titulo, gbc);

        gbc.gridy++;
        JLabel subtitulo = new JLabel("Acesse o sistema para continuar");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(Tema.TEXTO_SUAVE);
        card.add(subtitulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 2, 0);
        JLabel lblLogin = new JLabel("Login");
        lblLogin.setForeground(Tema.TEXTO_SUAVE);
        card.add(lblLogin, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 6, 0);
        txtLogin.setPreferredSize(new Dimension(0, 38));
        Tema.placeholder(txtLogin, "seu usuário");
        card.add(txtLogin, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setForeground(Tema.TEXTO_SUAVE);
        card.add(lblSenha, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 6, 0);
        txtSenha.setPreferredSize(new Dimension(0, 38));
        Tema.placeholder(txtSenha, "sua senha");
        card.add(txtSenha, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 6, 0);
        JButton btnEntrar = new JButton("Entrar");
        Tema.botaoPrimario(btnEntrar);
        btnEntrar.setPreferredSize(new Dimension(0, 42));
        btnEntrar.addActionListener(e -> entrar());
        card.add(btnEntrar, gbc);

        fundo.add(card);
        setContentPane(fundo);

        // Enter dispara o login
        txtSenha.addActionListener(e -> entrar());
        txtLogin.addActionListener(e -> txtSenha.requestFocusInWindow());
        getRootPane().setDefaultButton(btnEntrar);
    }

    private void entrar() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        try {
            Usuario u = controller.autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this,
                        "Login ou senha inválidos.",
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

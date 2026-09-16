package br.unip.erp.view;

import br.unip.erp.util.Sessao;
import br.unip.erp.util.Tema;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 * Janela principal (MDI) com o menu do sistema:
 * Cadastros (Cliente, Fornecedor, Forma de Pagamento, Usuario, Produto),
 * Movimentos (Venda, Compra) e Sair.
 */
public class MenuView extends JFrame {

    private final JDesktopPane desktop = new JDesktopPane();

    public MenuView() {
        setTitle("EMPRESA X - Gerenciamento de Compra e Venda");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        desktop.setBackground(Tema.FUNDO);
        setContentPane(desktop);
        setJMenuBar(criarMenu());
        exibirBoasVindas();
    }

    /** Mensagem de boas-vindas centralizada no fundo do desktop. */
    private void exibirBoasVindas() {
        JLabel banner = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "<span style='font-size:30px; color:#7c5ce8;'><b>EMPRESA X</b></span><br>"
                        + "<span style='font-size:13px; color:#969ba8;'>Bem-vindo, "
                        + Sessao.getUsuarioLogado()
                        + " &nbsp;·&nbsp; use o menu acima para começar</span>"
                        + "</div></html>", SwingConstants.CENTER);
        banner.setVerticalAlignment(SwingConstants.CENTER);
        banner.setBounds(0, 0, 980, 600);
        desktop.add(banner, Integer.valueOf(0));

        // mantem o banner centralizado quando a janela e redimensionada
        desktop.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                banner.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
            }
        });
    }

    private JMenuBar criarMenu() {
        JMenuBar barra = new JMenuBar();

        // --- Cadastros ---
        JMenu cadastros = new JMenu("Cadastros");
        JMenuItem miCliente = new JMenuItem("Cliente");
        JMenuItem miFornecedor = new JMenuItem("Fornecedor");
        JMenuItem miFormaPagto = new JMenuItem("Forma de Pagamento");
        JMenuItem miUsuario = new JMenuItem("Usuario");
        JMenuItem miProduto = new JMenuItem("Produto");
        miCliente.addActionListener(e -> abrir(new ClienteView()));
        miFornecedor.addActionListener(e -> abrir(new FornecedorView()));
        miFormaPagto.addActionListener(e -> abrir(new FormaPagamentoView()));
        miUsuario.addActionListener(e -> abrir(new UsuarioView()));
        miProduto.addActionListener(e -> abrir(new ProdutoView()));
        cadastros.add(miCliente);
        cadastros.add(miFornecedor);
        cadastros.add(miFormaPagto);
        cadastros.add(miUsuario);
        cadastros.add(miProduto);

        // --- Movimentos ---
        JMenu movimentos = new JMenu("Movimentos");
        JMenuItem miVenda = new JMenuItem("Venda");
        JMenuItem miCompra = new JMenuItem("Compra");
        miVenda.addActionListener(e -> abrir(new VendaView()));
        miCompra.addActionListener(e -> abrir(new CompraView()));
        movimentos.add(miVenda);
        movimentos.add(miCompra);

        // --- Sair ---
        JMenu sair = new JMenu("Sair");
        JMenuItem miLogout = new JMenuItem("Trocar usuario");
        JMenuItem miEncerrar = new JMenuItem("Encerrar");
        miLogout.addActionListener(e -> logout());
        miEncerrar.addActionListener(e -> encerrar());
        sair.add(miLogout);
        sair.add(miEncerrar);

        barra.add(cadastros);
        barra.add(movimentos);
        barra.add(sair);
        return barra;
    }

    private void abrir(JInternalFrame frame) {
        // evita abrir a mesma janela duas vezes
        for (JInternalFrame f : desktop.getAllFrames()) {
            if (f.getClass().equals(frame.getClass())) {
                f.toFront();
                return;
            }
        }
        desktop.add(frame);
        frame.setVisible(true);
        frame.toFront();
    }

    private void logout() {
        Sessao.encerrar();
        dispose();
        new LoginView().setVisible(true);
    }

    private void encerrar() {
        int op = JOptionPane.showConfirmDialog(this,
                "Deseja realmente encerrar o sistema?", "Sair",
                JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}

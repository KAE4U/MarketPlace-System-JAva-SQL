package br.unip.erp.view;

import br.unip.erp.controller.UsuarioController;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/** Formulario de cadastro de USUARIO. */
public class UsuarioView extends JInternalFrame {

    private final UsuarioController controller = new UsuarioController();

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtLogin = new JTextField(15);
    private final JPasswordField txtSenha = new JPasswordField(15);
    private final JCheckBox chkAtivo = new JCheckBox("Ativo", true);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Codigo", "Nome", "Login", "Ativo"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public UsuarioView() {
        super("Cadastro de Usuario", true, true, true, true);
        setSize(600, 440);
        montar();
        carregarTabela();
    }

    private void montar() {
        txtCodigo.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Codigo:"), g);
        g.gridx = 1; form.add(txtCodigo, g);
        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Nome:"), g);
        g.gridx = 1; form.add(txtNome, g);
        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Login:"), g);
        g.gridx = 1; form.add(txtLogin, g);
        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Senha:"), g);
        g.gridx = 1; form.add(txtSenha, g);
        g.gridx = 1; g.gridy = 4; form.add(chkAtivo, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btNovo = new JButton("Novo");
        JButton btSalvar = new JButton("Salvar");
        JButton btExcluir = new JButton("Excluir");
        Tema.botaoSecundario(btNovo);
        Tema.botaoPrimario(btSalvar);
        Tema.botaoPerigo(btExcluir);
        btNovo.addActionListener(e -> limpar());
        btSalvar.addActionListener(e -> salvar());
        btExcluir.addActionListener(e -> excluir());
        botoes.add(btNovo);
        botoes.add(btSalvar);
        botoes.add(btExcluir);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> selecionar());
        Tema.estilizarTabela(tabela);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Usuario> lista = controller.listar();
        for (Usuario u : lista) {
            tableModel.addRow(new Object[]{u.getCodigo(), u.getNome(), u.getLogin(), u.getAtivo()});
        }
    }

    private void selecionar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        Usuario u = controller.buscar(codigo);
        if (u != null) {
            txtCodigo.setText(String.valueOf(u.getCodigo()));
            txtNome.setText(u.getNome());
            txtLogin.setText(u.getLogin());
            txtSenha.setText(u.getSenha());
            chkAtivo.setSelected("S".equals(u.getAtivo()));
        }
    }

    private void limpar() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Usuario u = new Usuario();
            if (!txtCodigo.getText().isBlank()) {
                u.setCodigo(Integer.parseInt(txtCodigo.getText()));
            }
            u.setNome(txtNome.getText());
            u.setLogin(txtLogin.getText());
            u.setSenha(new String(txtSenha.getPassword()));
            u.setAtivo(chkAtivo.isSelected() ? "S" : "N");
            controller.salvar(u);
            JOptionPane.showMessageDialog(this, "Usuario salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione um usuario para excluir.");
            return;
        }
        try {
            controller.excluir(Integer.parseInt(txtCodigo.getText()));
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

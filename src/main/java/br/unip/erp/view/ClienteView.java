package br.unip.erp.view;

import br.unip.erp.controller.ClienteController;
import br.unip.erp.model.Cliente;
import br.unip.erp.model.Pessoa;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import java.math.BigDecimal;
import java.util.List;

/** Formulario de cadastro de CLIENTE (com dados de PESSOA). */
public class ClienteView extends JInternalFrame {

    private final ClienteController controller = new ClienteController();

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNome = new JTextField(25);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Fisica", "Juridica"});
    private final JTextField txtCpfCnpj = new JTextField(18);
    private final JTextField txtCidade = new JTextField(20);
    private final JTextField txtUf = new JTextField(3);
    private final JTextField txtFone = new JTextField(15);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtLimite = new JTextField(10);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Codigo", "Nome", "CPF/CNPJ", "Cidade", "Limite"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public ClienteView() {
        super("Cadastro de Cliente", true, true, true, true);
        setSize(720, 520);
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
        g.gridx = 2; form.add(new JLabel("Tipo:"), g);
        g.gridx = 3; form.add(cbTipo, g);

        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Nome:"), g);
        g.gridx = 1; g.gridwidth = 3; form.add(txtNome, g); g.gridwidth = 1;

        g.gridx = 0; g.gridy = 2; form.add(new JLabel("CPF/CNPJ:"), g);
        g.gridx = 1; form.add(txtCpfCnpj, g);
        g.gridx = 2; form.add(new JLabel("Limite cred.:"), g);
        g.gridx = 3; form.add(txtLimite, g);

        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Cidade:"), g);
        g.gridx = 1; form.add(txtCidade, g);
        g.gridx = 2; form.add(new JLabel("UF:"), g);
        g.gridx = 3; form.add(txtUf, g);

        g.gridx = 0; g.gridy = 4; form.add(new JLabel("Telefone:"), g);
        g.gridx = 1; form.add(txtFone, g);
        g.gridx = 2; form.add(new JLabel("E-mail:"), g);
        g.gridx = 3; form.add(txtEmail, g);

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
        List<Cliente> lista = controller.listar();
        for (Cliente c : lista) {
            Pessoa p = c.getPessoa();
            tableModel.addRow(new Object[]{
                    c.getCodigo(), p.getNome(), p.getCpfCnpj(), p.getCidade(), c.getLimiteCredito()});
        }
    }

    private void selecionar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        Cliente c = controller.buscar(codigo);
        if (c != null) {
            Pessoa p = c.getPessoa();
            txtCodigo.setText(String.valueOf(c.getCodigo()));
            txtNome.setText(p.getNome());
            cbTipo.setSelectedIndex("J".equals(p.getFisica()) ? 1 : 0);
            txtCpfCnpj.setText(p.getCpfCnpj());
            txtCidade.setText(p.getCidade());
            txtUf.setText(p.getUf());
            txtFone.setText(p.getFone1());
            txtEmail.setText(p.getEmail());
            txtLimite.setText(c.getLimiteCredito() == null ? "0" : c.getLimiteCredito().toPlainString());
        }
    }

    private void limpar() {
        txtCodigo.setText("");
        txtNome.setText("");
        cbTipo.setSelectedIndex(0);
        txtCpfCnpj.setText("");
        txtCidade.setText("");
        txtUf.setText("");
        txtFone.setText("");
        txtEmail.setText("");
        txtLimite.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Cliente c = new Cliente();
            Pessoa p = new Pessoa();
            if (!txtCodigo.getText().isBlank()) {
                c.setCodigo(Integer.parseInt(txtCodigo.getText()));
                // recupera o codigo da pessoa vinculada
                Cliente atual = controller.buscar(c.getCodigo());
                if (atual != null) {
                    p.setCodigo(atual.getPessoa().getCodigo());
                }
            }
            p.setNome(txtNome.getText());
            p.setFisica(cbTipo.getSelectedIndex() == 1 ? "J" : "F");
            p.setCpfCnpj(txtCpfCnpj.getText());
            p.setCidade(txtCidade.getText());
            p.setUf(txtUf.getText());
            p.setFone1(txtFone.getText());
            p.setEmail(txtEmail.getText());
            c.setPessoa(p);
            c.setLimiteCredito(txtLimite.getText().isBlank()
                    ? BigDecimal.ZERO
                    : new BigDecimal(txtLimite.getText().trim().replace(",", ".")));
            controller.salvar(c);
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Limite de credito invalido.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
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

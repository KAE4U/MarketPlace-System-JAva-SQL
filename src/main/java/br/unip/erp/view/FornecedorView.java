package br.unip.erp.view;

import br.unip.erp.controller.FornecedorController;
import br.unip.erp.model.Fornecedor;
import br.unip.erp.model.Pessoa;
import br.unip.erp.util.Mascaras;
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
import java.util.List;

/** Formulario de cadastro de FORNECEDOR (com dados de PESSOA). */
public class FornecedorView extends JInternalFrame {

    private final FornecedorController controller = new FornecedorController();

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNome = new JTextField(25);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Fisica", "Juridica"});
    private final JTextField txtCpfCnpj = new JTextField(18);
    private final JTextField txtCidade = new JTextField(20);
    private final JTextField txtUf = new JTextField(3);
    private final JTextField txtFone = new JTextField(15);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtContato = new JTextField(20);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Codigo", "Nome", "CPF/CNPJ", "Cidade", "Contato"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public FornecedorView() {
        super("Cadastro de Fornecedor", true, true, true, true);
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
        g.gridx = 2; form.add(new JLabel("Contato:"), g);
        g.gridx = 3; form.add(txtContato, g);

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

        // Mascaras dinamicas
        Mascaras.cpfCnpj(txtCpfCnpj);
        Mascaras.telefone(txtFone);

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
        List<Fornecedor> lista = controller.listar();
        for (Fornecedor f : lista) {
            Pessoa p = f.getPessoa();
            tableModel.addRow(new Object[]{
                    f.getCodigo(), p.getNome(), p.getCpfCnpj(), p.getCidade(), f.getContato()});
        }
    }

    private void selecionar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        Fornecedor f = controller.buscar(codigo);
        if (f != null) {
            Pessoa p = f.getPessoa();
            txtCodigo.setText(String.valueOf(f.getCodigo()));
            txtNome.setText(p.getNome());
            cbTipo.setSelectedIndex("J".equals(p.getFisica()) ? 1 : 0);
            txtCpfCnpj.setText(p.getCpfCnpj());
            txtCidade.setText(p.getCidade());
            txtUf.setText(p.getUf());
            txtFone.setText(p.getFone1());
            txtEmail.setText(p.getEmail());
            txtContato.setText(f.getContato());
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
        txtContato.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Fornecedor f = new Fornecedor();
            Pessoa p = new Pessoa();
            if (!txtCodigo.getText().isBlank()) {
                f.setCodigo(Integer.parseInt(txtCodigo.getText()));
                Fornecedor atual = controller.buscar(f.getCodigo());
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
            f.setPessoa(p);
            f.setContato(txtContato.getText());
            controller.salvar(f);
            JOptionPane.showMessageDialog(this, "Fornecedor salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (!br.unip.erp.util.Permissao.podeExcluir(this)) {
            return;
        }
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor para excluir.");
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

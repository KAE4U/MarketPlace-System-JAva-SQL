package br.unip.erp.view;

import br.unip.erp.controller.FormaPagamentoController;
import br.unip.erp.model.FormaPagamento;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

/** Formulario de cadastro de FORMA DE PAGAMENTO. */
public class FormaPagamentoView extends JInternalFrame {

    private final FormaPagamentoController controller = new FormaPagamentoController();

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNome = new JTextField(25);
    private final JCheckBox chkAtivo = new JCheckBox("Ativo", true);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Codigo", "Nome", "Ativo"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public FormaPagamentoView() {
        super("Cadastro de Forma de Pagamento", true, true, true, true);
        setSize(560, 420);
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
        g.gridx = 1; g.gridy = 2; form.add(chkAtivo, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btNovo = new JButton("Novo");
        JButton btSalvar = new JButton("Salvar");
        JButton btExcluir = new JButton("Excluir");
        btNovo.addActionListener(e -> limpar());
        btSalvar.addActionListener(e -> salvar());
        btExcluir.addActionListener(e -> excluir());
        botoes.add(btNovo);
        botoes.add(btSalvar);
        botoes.add(btExcluir);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> selecionar());

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<FormaPagamento> lista = controller.listar();
        for (FormaPagamento f : lista) {
            tableModel.addRow(new Object[]{f.getCodigo(), f.getNome(), f.getAtivo()});
        }
    }

    private void selecionar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtCodigo.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtNome.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        chkAtivo.setSelected("S".equals(tableModel.getValueAt(row, 2)));
    }

    private void limpar() {
        txtCodigo.setText("");
        txtNome.setText("");
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            FormaPagamento f = new FormaPagamento();
            if (!txtCodigo.getText().isBlank()) {
                f.setCodigo(Integer.parseInt(txtCodigo.getText()));
            }
            f.setNome(txtNome.getText());
            f.setAtivo(chkAtivo.isSelected() ? "S" : "N");
            controller.salvar(f);
            JOptionPane.showMessageDialog(this, "Registro salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione um registro para excluir.");
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

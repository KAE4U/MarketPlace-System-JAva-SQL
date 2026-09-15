package br.unip.erp.view;

import br.unip.erp.controller.ProdutoController;
import br.unip.erp.model.Produto;

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
import java.math.BigDecimal;
import java.util.List;

/** Formulario de cadastro de PRODUTO. */
public class ProdutoView extends JInternalFrame {

    private final ProdutoController controller = new ProdutoController();

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtUnidade = new JTextField(5);
    private final JTextField txtEstoque = new JTextField(8);
    private final JTextField txtPreco = new JTextField(8);
    private final JTextField txtCusto = new JTextField(8);
    private final JCheckBox chkAtivo = new JCheckBox("Ativo", true);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Codigo", "Nome", "Un", "Estoque", "Preco"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public ProdutoView() {
        super("Cadastro de Produto", true, true, true, true);
        setSize(680, 480);
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
        g.gridx = 1; g.gridwidth = 3; form.add(txtNome, g); g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Unidade:"), g);
        g.gridx = 1; form.add(txtUnidade, g);
        g.gridx = 2; form.add(new JLabel("Estoque:"), g);
        g.gridx = 3; form.add(txtEstoque, g);
        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Preco:"), g);
        g.gridx = 1; form.add(txtPreco, g);
        g.gridx = 2; form.add(new JLabel("Custo:"), g);
        g.gridx = 3; form.add(txtCusto, g);
        g.gridx = 1; g.gridy = 4; form.add(chkAtivo, g);

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
        List<Produto> lista = controller.listar();
        for (Produto p : lista) {
            tableModel.addRow(new Object[]{
                    p.getCodigo(), p.getNome(), p.getUnidade(), p.getEstoque(), p.getPreco()});
        }
    }

    private void selecionar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        Produto p = controller.buscar(codigo);
        if (p != null) {
            txtCodigo.setText(String.valueOf(p.getCodigo()));
            txtNome.setText(p.getNome());
            txtUnidade.setText(p.getUnidade());
            txtEstoque.setText(valor(p.getEstoque()));
            txtPreco.setText(valor(p.getPreco()));
            txtCusto.setText(valor(p.getCusto()));
            chkAtivo.setSelected("S".equals(p.getAtivo()));
        }
    }

    private String valor(BigDecimal b) {
        return b == null ? "0" : b.toPlainString();
    }

    private BigDecimal parse(String s) {
        if (s == null || s.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s.trim().replace(",", "."));
    }

    private void limpar() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtUnidade.setText("");
        txtEstoque.setText("");
        txtPreco.setText("");
        txtCusto.setText("");
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Produto p = new Produto();
            if (!txtCodigo.getText().isBlank()) {
                p.setCodigo(Integer.parseInt(txtCodigo.getText()));
            }
            p.setNome(txtNome.getText());
            p.setUnidade(txtUnidade.getText());
            p.setEstoque(parse(txtEstoque.getText()));
            p.setPreco(parse(txtPreco.getText()));
            p.setCusto(parse(txtCusto.getText()));
            p.setAtivo(chkAtivo.isSelected() ? "S" : "N");
            controller.salvar(p);
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso.");
            limpar();
            carregarTabela();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Valores numericos invalidos.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtCodigo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
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

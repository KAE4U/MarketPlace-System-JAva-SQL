package br.unip.erp.view;

import br.unip.erp.controller.CompraController;
import br.unip.erp.controller.FornecedorController;
import br.unip.erp.controller.ProdutoController;
import br.unip.erp.model.Compra;
import br.unip.erp.model.CompraProduto;
import br.unip.erp.model.Fornecedor;
import br.unip.erp.model.Produto;
import br.unip.erp.util.Sessao;
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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;

/** Formulario de COMPRA: cabecalho e itens (produtos). */
public class CompraView extends JInternalFrame {

    private final CompraController compraController = new CompraController();
    private final FornecedorController fornecedorController = new FornecedorController();
    private final ProdutoController produtoController = new ProdutoController();

    private final JComboBox<Fornecedor> cbFornecedor = new JComboBox<>();
    private final JComboBox<Produto> cbProduto = new JComboBox<>();
    private final JTextField txtQtde = new JTextField(5);
    private final JTextField txtPreco = new JTextField(8);
    private final JLabel lblTotal = new JLabel("Total: R$ 0,00");

    private final DefaultTableModel modelItens =
            new DefaultTableModel(new Object[]{"Produto", "Qtde", "Preco", "Total"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaItens = new JTable(modelItens);

    private final Compra compra = new Compra();

    public CompraView() {
        super("Compra", true, true, true, true);
        setSize(760, 540);
        compra.setUsuario(Sessao.getUsuarioLogado());
        montar();
        carregarCombos();
    }

    private void montar() {
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cabecalho.setBorder(BorderFactory.createTitledBorder("Dados da Compra"));
        cabecalho.add(new JLabel("Fornecedor:"));
        cabecalho.add(cbFornecedor);

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Produtos"));
        JPanel addItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addItem.add(new JLabel("Produto:"));
        addItem.add(cbProduto);
        addItem.add(new JLabel("Qtde:"));
        addItem.add(txtQtde);
        addItem.add(new JLabel("Preco:"));
        addItem.add(txtPreco);
        JButton btAddItem = new JButton("Adicionar");
        JButton btDelItem = new JButton("Remover");
        Tema.botaoSecundario(btAddItem);
        Tema.botaoSecundario(btDelItem);
        btAddItem.addActionListener(e -> adicionarItem());
        btDelItem.addActionListener(e -> removerItem());
        addItem.add(btAddItem);
        addItem.add(btDelItem);
        Tema.estilizarTabela(tabelaItens);
        painelItens.add(addItem, BorderLayout.NORTH);
        painelItens.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        lblTotal.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
        lblTotal.setForeground(Tema.SUCESSO);
        rodape.add(lblTotal);
        JButton btFinalizar = new JButton("Finalizar Compra");
        JButton btNova = new JButton("Nova");
        Tema.botaoPrimario(btFinalizar);
        Tema.botaoSecundario(btNova);
        btFinalizar.addActionListener(e -> finalizar());
        btNova.addActionListener(e -> novaCompra());
        rodape.add(btNova);
        rodape.add(btFinalizar);

        setLayout(new BorderLayout());
        add(cabecalho, BorderLayout.NORTH);
        add(painelItens, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarCombos() {
        cbFornecedor.removeAllItems();
        for (Fornecedor f : fornecedorController.listar()) {
            cbFornecedor.addItem(f);
        }
        cbProduto.removeAllItems();
        for (Produto p : produtoController.listar()) {
            cbProduto.addItem(p);
        }
    }

    private BigDecimal parse(String s) {
        if (s == null || s.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s.trim().replace(",", "."));
    }

    private void adicionarItem() {
        try {
            Produto p = (Produto) cbProduto.getSelectedItem();
            if (p == null) {
                return;
            }
            BigDecimal qtde = parse(txtQtde.getText());
            if (qtde.signum() <= 0) {
                JOptionPane.showMessageDialog(this, "Informe uma quantidade valida.");
                return;
            }
            BigDecimal preco = txtPreco.getText().isBlank()
                    ? (p.getCusto() == null ? BigDecimal.ZERO : p.getCusto())
                    : parse(txtPreco.getText());
            CompraProduto item = new CompraProduto();
            item.setProduto(p);
            item.setQtde(qtde);
            item.setPreco(preco);
            item.setDesconto(BigDecimal.ZERO);
            item.setTotal(preco.multiply(qtde));
            compra.getItens().add(item);
            modelItens.addRow(new Object[]{p.getNome(), qtde, preco, item.getTotal()});
            txtQtde.setText("");
            txtPreco.setText("");
            atualizarTotal();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Valores invalidos.");
        }
    }

    private void removerItem() {
        int row = tabelaItens.getSelectedRow();
        if (row >= 0) {
            compra.getItens().remove(row);
            modelItens.removeRow(row);
            atualizarTotal();
        }
    }

    private void atualizarTotal() {
        compraController.recalcularTotais(compra);
        lblTotal.setText("Total: " + br.unip.erp.util.Mascaras.moeda(compra.getTotal()));
    }

    private void finalizar() {
        try {
            Fornecedor f = (Fornecedor) cbFornecedor.getSelectedItem();
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Selecione um fornecedor.");
                return;
            }
            compra.setFornecedor(f);
            compra.setUsuario(Sessao.getUsuarioLogado());
            int codigo = compraController.registrar(compra);
            JOptionPane.showMessageDialog(this,
                    "Compra #" + codigo + " registrada com sucesso!\nTotal: "
                            + br.unip.erp.util.Mascaras.moeda(compra.getTotal()));
            novaCompra();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novaCompra() {
        compra.getItens().clear();
        compra.setCodigo(0);
        modelItens.setRowCount(0);
        atualizarTotal();
    }
}

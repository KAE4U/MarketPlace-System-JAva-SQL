package br.unip.erp.view;

import br.unip.erp.controller.ClienteController;
import br.unip.erp.controller.FormaPagamentoController;
import br.unip.erp.controller.ProdutoController;
import br.unip.erp.controller.VendaController;
import br.unip.erp.model.Cliente;
import br.unip.erp.model.FormaPagamento;
import br.unip.erp.model.Produto;
import br.unip.erp.model.Venda;
import br.unip.erp.model.VendaPagamento;
import br.unip.erp.model.VendaProduto;
import br.unip.erp.util.Sessao;

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
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

/** Formulario de VENDA: cabecalho, itens (produtos) e formas de pagamento. */
public class VendaView extends JInternalFrame {

    private final VendaController vendaController = new VendaController();
    private final ClienteController clienteController = new ClienteController();
    private final ProdutoController produtoController = new ProdutoController();
    private final FormaPagamentoController formaController = new FormaPagamentoController();

    private final JComboBox<Cliente> cbCliente = new JComboBox<>();
    private final JComboBox<Produto> cbProduto = new JComboBox<>();
    private final JTextField txtQtde = new JTextField(5);
    private final JComboBox<FormaPagamento> cbForma = new JComboBox<>();
    private final JTextField txtValorPg = new JTextField(8);
    private final JLabel lblTotal = new JLabel("Total: R$ 0,00");

    private final DefaultTableModel modelItens =
            new DefaultTableModel(new Object[]{"Produto", "Qtde", "Preco", "Total"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaItens = new JTable(modelItens);

    private final DefaultTableModel modelPagtos =
            new DefaultTableModel(new Object[]{"Forma", "Valor"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaPagtos = new JTable(modelPagtos);

    private final Venda venda = new Venda();

    public VendaView() {
        super("Venda", true, true, true, true);
        setSize(780, 600);
        venda.setUsuario(Sessao.getUsuarioLogado());
        montar();
        carregarCombos();
    }

    private void montar() {
        // Cabecalho
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cabecalho.setBorder(BorderFactory.createTitledBorder("Dados da Venda"));
        cabecalho.add(new JLabel("Cliente:"));
        cabecalho.add(cbCliente);

        // Itens
        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Produtos"));
        JPanel addItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addItem.add(new JLabel("Produto:"));
        addItem.add(cbProduto);
        addItem.add(new JLabel("Qtde:"));
        addItem.add(txtQtde);
        JButton btAddItem = new JButton("Adicionar");
        JButton btDelItem = new JButton("Remover");
        btAddItem.addActionListener(e -> adicionarItem());
        btDelItem.addActionListener(e -> removerItem());
        addItem.add(btAddItem);
        addItem.add(btDelItem);
        painelItens.add(addItem, BorderLayout.NORTH);
        painelItens.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        // Pagamentos
        JPanel painelPg = new JPanel(new BorderLayout());
        painelPg.setBorder(BorderFactory.createTitledBorder("Formas de Pagamento"));
        JPanel addPg = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPg.add(new JLabel("Forma:"));
        addPg.add(cbForma);
        addPg.add(new JLabel("Valor:"));
        addPg.add(txtValorPg);
        JButton btAddPg = new JButton("Adicionar");
        JButton btDelPg = new JButton("Remover");
        btAddPg.addActionListener(e -> adicionarPagamento());
        btDelPg.addActionListener(e -> removerPagamento());
        addPg.add(btAddPg);
        addPg.add(btDelPg);
        painelPg.add(addPg, BorderLayout.NORTH);
        painelPg.add(new JScrollPane(tabelaPagtos), BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(2, 1));
        centro.add(painelItens);
        centro.add(painelPg);

        // Rodape
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(lblTotal);
        JButton btFinalizar = new JButton("Finalizar Venda");
        JButton btNova = new JButton("Nova");
        btFinalizar.addActionListener(e -> finalizar());
        btNova.addActionListener(e -> novaVenda());
        rodape.add(btNova);
        rodape.add(btFinalizar);

        setLayout(new BorderLayout());
        add(cabecalho, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarCombos() {
        cbCliente.removeAllItems();
        for (Cliente c : clienteController.listar()) {
            cbCliente.addItem(c);
        }
        cbProduto.removeAllItems();
        for (Produto p : produtoController.listar()) {
            cbProduto.addItem(p);
        }
        cbForma.removeAllItems();
        for (FormaPagamento f : formaController.listar()) {
            cbForma.addItem(f);
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
            VendaProduto item = new VendaProduto();
            item.setProduto(p);
            item.setQtde(qtde);
            item.setPreco(p.getPreco() == null ? BigDecimal.ZERO : p.getPreco());
            item.setDesconto(BigDecimal.ZERO);
            item.setTotal(item.getPreco().multiply(qtde));
            venda.getItens().add(item);
            modelItens.addRow(new Object[]{p.getNome(), qtde, item.getPreco(), item.getTotal()});
            txtQtde.setText("");
            atualizarTotal();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantidade invalida.");
        }
    }

    private void removerItem() {
        int row = tabelaItens.getSelectedRow();
        if (row >= 0) {
            venda.getItens().remove(row);
            modelItens.removeRow(row);
            atualizarTotal();
        }
    }

    private void adicionarPagamento() {
        try {
            FormaPagamento f = (FormaPagamento) cbForma.getSelectedItem();
            if (f == null) {
                return;
            }
            BigDecimal valor = parse(txtValorPg.getText());
            VendaPagamento pg = new VendaPagamento();
            pg.setFormaPagamento(f);
            pg.setValor(valor);
            venda.getPagamentos().add(pg);
            modelPagtos.addRow(new Object[]{f.getNome(), valor});
            txtValorPg.setText("");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Valor invalido.");
        }
    }

    private void removerPagamento() {
        int row = tabelaPagtos.getSelectedRow();
        if (row >= 0) {
            venda.getPagamentos().remove(row);
            modelPagtos.removeRow(row);
        }
    }

    private void atualizarTotal() {
        vendaController.recalcularTotais(venda);
        lblTotal.setText("Total: R$ " + venda.getTotal().toPlainString());
    }

    private void finalizar() {
        try {
            Cliente c = (Cliente) cbCliente.getSelectedItem();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente.");
                return;
            }
            venda.setCliente(c);
            venda.setUsuario(Sessao.getUsuarioLogado());
            int codigo = vendaController.registrar(venda);
            JOptionPane.showMessageDialog(this,
                    "Venda #" + codigo + " registrada com sucesso!\nTotal: R$ "
                            + venda.getTotal().toPlainString());
            novaVenda();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novaVenda() {
        venda.getItens().clear();
        venda.getPagamentos().clear();
        venda.setCodigo(0);
        modelItens.setRowCount(0);
        modelPagtos.setRowCount(0);
        atualizarTotal();
    }
}
